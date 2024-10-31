package io.karma.moreprotectables.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.block.ReinforcedShadowBlock;
import io.karma.moreprotectables.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.EmptyTextureStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 26/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ReinforcedShadowBlockItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(MoreProtectables.MODID,
        "textures/gui/shield.png");
    // @formatter:off
    private static final RenderType OVERLAY_RENDER_TYPE = RenderType.create(
        String.format("%s:reinfored_block_overlay", MoreProtectables.MODID),
        DefaultVertexFormat.POSITION_TEX,
        Mode.QUADS,
        128,
        false,
        false,
        CompositeState.builder()
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setShaderState(RenderStateShard.POSITION_TEX_SHADER)
            .setCullState(RenderStateShard.CULL)
            .setTextureState(new EmptyTextureStateShard(
                () -> RenderSystem.setShaderTexture(0, OVERLAY_TEXTURE),
                () -> {}
            ))
            .createCompositeState(false)
    );
    // @formatter:on

    private ReinforcedShadowBlockItemRenderer(final BlockEntityRenderDispatcher dispatcher,
                                              final EntityModelSet entityModels) {
        super(dispatcher, entityModels);
    }

    public static IClientItemExtensions extension() {
        return new IClientItemExtensions() {
            private final Lazy<BlockEntityWithoutLevelRenderer> renderer = Lazy.of(() -> {
                final var game = Minecraft.getInstance();
                return new ReinforcedShadowBlockItemRenderer(game.getBlockEntityRenderDispatcher(),
                    game.getEntityModels());
            });

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer.get();
            }
        };
    }

    @Override
    public void renderByItem(final @NotNull ItemStack stack,
                             final @NotNull ItemDisplayContext displayContext,
                             final @NotNull PoseStack poseStack,
                             final @NotNull MultiBufferSource bufferSource,
                             final int packedLight,
                             final int packedOverlay) {
        final var item = stack.getItem();
        final var block = Block.byItem(item);
        if (!(block instanceof ReinforcedShadowBlock shadowBlock)) {
            return;
        }

        final var game = Minecraft.getInstance();
        final var itemRenderer = game.getItemRenderer();
        final var blockRenderer = game.getBlockRenderer();
        final var shadowStack = new ItemStack(shadowBlock.getVanillaBlock());
        final var shadowItem = shadowStack.getItem();

        var model = itemRenderer.getItemModelShaper().getItemModel(shadowItem);
        if (model == null) {
            // So we can render something if there's no item model for this block
            model = blockRenderer.getBlockModel(Block.byItem(shadowItem).defaultBlockState());
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        itemRenderer.render(shadowStack,
            displayContext,
            false,
            poseStack,
            bufferSource,
            packedLight,
            packedOverlay,
            model);
        poseStack.popPose();
        // Render GUI overlay
        if (displayContext == ItemDisplayContext.GUI) {
            poseStack.pushPose();
            RenderUtils.drawQuad(poseStack, bufferSource.getBuffer(OVERLAY_RENDER_TYPE), 0.7F, 0.7F, 0.25F, 0.25F, 2F);
            poseStack.popPose();
        }
    }
}
