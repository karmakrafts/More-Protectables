package io.karma.moreprotectables.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.init.ModItems;
import io.karma.moreprotectables.item.ABRItem;
import io.karma.moreprotectables.util.ABRSelectionUtils;
import io.karma.moreprotectables.client.utils.RenderUtils;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * @author Alexander Hinze
 * @since 01/11/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ABROverlayRenderer {
    public static final ABROverlayRenderer INSTANCE = new ABROverlayRenderer();
    // @formatter:off
    private static final RenderType RENDER_TYPE = RenderType.create(
        String.format("%s:abr_block_overlay", MoreProtectables.MODID),
        DefaultVertexFormat.POSITION_COLOR,
        Mode.QUADS,
        1048576, // 1MB
        true,
        true,
        CompositeState.builder()
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setCullState(RenderStateShard.NO_CULL)
            .setLayeringState(RenderStateShard.POLYGON_OFFSET_LAYERING)
            .createCompositeState(true)
    );
    private final BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
    // @formatter:on
    private int overlayTicks;
    private float overlayOpacity;

    // @formatter:off
    private ABROverlayRenderer() {}
    // @formatter:on

    @Internal
    public void init() {
        final var bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::onRenderWorldLast);
        bus.addListener(this::onClientTick);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            final var target = Minecraft.getInstance().getMainRenderTarget();
            if (target.isStencilEnabled()) {
                return;
            }
            MoreProtectables.LOGGER.info("Enabling stencil buffer for main render target");
            target.enableStencil();
        });
    }

    private int getOverlayRadius() {
        final var player = Minecraft.getInstance().player;
        if (player == null) {
            return 0;
        }
        for (final var hand : InteractionHand.values()) {
            final var stack = player.getItemInHand(hand);
            for (final var type : ABRItem.Type.values()) {
                if (stack.getItem() != ModItems.ADVANCED_BLOCK_REINFORCER.get(type).get()) {
                    continue;
                }
                return type.getRadius();
            }
            return 0;
        }
        return 0;
    }

    private boolean shouldRenderOverlay() {
        return getOverlayRadius() > 0;
    }

    private void onClientTick(final ClientTickEvent event) {
        if (event.phase == Phase.END && shouldRenderOverlay() && !Minecraft.getInstance().isPaused()) {
            overlayTicks++;
        }
    }

    private BakedModel getOverlayModel(final BlockState state) {
        final var bms = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
        if (state.getBlock() instanceof IReinforcedBlock reinforcedBlock) {
            final var actualState = reinforcedBlock.getVanillaBlock().withPropertiesOf(state);
            return bms.getBlockModel(actualState);
        }
        return bms.getBlockModel(state);
    }

    private void onRenderWorldLast(final RenderLevelStageEvent event) {
        if (event.getStage() != Stage.AFTER_PARTICLES) {
            return;
        }

        final var radius = getOverlayRadius();
        if (radius == 0) {
            return;
        }

        final var game = Minecraft.getInstance();
        final var level = game.level;
        if (level == null) {
            return;
        }

        final var hitResult = game.hitResult;
        if (hitResult == null || hitResult.getType() != Type.BLOCK) {
            return;
        }

        final var cameraPos = event.getCamera().getPosition();
        final var hitPos = ((BlockHitResult) hitResult).getBlockPos();
        final var poseStack = event.getPoseStack();

        final var partialTick = event.getPartialTick();
        final var previousOpacity = overlayOpacity;
        overlayOpacity = 0.1F + ((float) Math.sin((float) overlayTicks / 5F) + 1F) * 0.25F;
        final var overlayOpacity = previousOpacity + (this.overlayOpacity - previousOpacity) * partialTick;

        final var buffer = bufferSource.getBuffer(RENDER_TYPE);
        final var delegatingSource = DelegatingBufferSource.get(buffer);
        final var blockRenderer = game.getBlockRenderer();
        final var blockEntityRendererDispatcher = game.getBlockEntityRenderDispatcher();

        RenderSystem.setShaderColor(0.2F, 0.3F, 1F, overlayOpacity);
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        ABRSelectionUtils.findConnectedBlocks(hitPos, level, radius, pos -> {
            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

            // Render the regular block model
            final var state = level.getBlockState(pos);
            final var model = getOverlayModel(state);
            final var modelData = model.getModelData(level, pos, state, ModelData.EMPTY);
            for (final var type : model.getRenderTypes(state, level.random, modelData)) {
                blockRenderer.getModelRenderer().renderModel(poseStack.last(),
                    buffer,
                    state,
                    model,
                    1F,
                    1F,
                    1F,
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    modelData,
                    type);
            }

            // Render block entity part if present
            final var blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                blockEntityRendererDispatcher.render(blockEntity, partialTick, poseStack, delegatingSource);
            }

            poseStack.popPose();
        });

        RenderUtils.enableOverlayStencil();
        bufferSource.endBatch();
        RenderUtils.disableOverlayStencil();

        poseStack.popPose();
    }
}
