package io.karma.moreprotectables.client.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.client.event.BlockEntityItemRenderEvent;
import io.karma.moreprotectables.client.event.BlockEntityRenderEvent;
import io.karma.moreprotectables.util.KeypadChestBlock;
import io.karma.moreprotectables.util.KeypadChestBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 14/10/2024
 */
public final class ChestKeypadRenderer {
    public static final ChestKeypadRenderer INSTANCE = new ChestKeypadRenderer();
    public static final ResourceLocation KEYPAD_MODEL = new ResourceLocation(MoreProtectables.MODID,
        "block/keypad_on_chest");
    public static final ResourceLocation KEYPAD_UNLOCKED_MODEL = new ResourceLocation(MoreProtectables.MODID,
        "block/keypad_on_chest_unlocked");
    public static final ResourceLocation KEYPAD_LOCKED_MODEL = new ResourceLocation(MoreProtectables.MODID,
        "block/keypad_on_chest_locked");

    private final BufferBuilder bufferBuilder = new BufferBuilder(256);
    private final HashMap<RenderType, BufferBuilder> bufferBuilders = new HashMap<>();
    private final BufferSource bufferSource = MultiBufferSource.immediateWithBuffers(bufferBuilders, bufferBuilder);

    private BakedModel keypadModel;
    private BakedModel keypadUnlockedModel;
    private BakedModel keypadLockedModel;

    // @formatter:off
    private ChestKeypadRenderer() {}
    // @formatter:on

    @Internal
    public void setup() {
        final var bus = MinecraftForge.EVENT_BUS;
        final var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onRegisterAdditionalModels);
        modBus.addListener(this::onBakingComplete);
        bus.addListener(this::onRenderBlockEntity);
        bus.addListener(this::onRenderBlockEntityItem);
    }

    private void onRegisterAdditionalModels(final ModelEvent.RegisterAdditional event) {
        MoreProtectables.LOGGER.info("Registering additional models");
        event.register(KEYPAD_MODEL);
        event.register(KEYPAD_UNLOCKED_MODEL);
        event.register(KEYPAD_LOCKED_MODEL);
    }

    private void onBakingComplete(final ModelEvent.BakingCompleted event) {
        MoreProtectables.LOGGER.info("Setting up additional models");
        final var models = event.getModels();
        keypadModel = models.get(KEYPAD_MODEL);
        keypadUnlockedModel = models.get(KEYPAD_UNLOCKED_MODEL);
        keypadLockedModel = models.get(KEYPAD_LOCKED_MODEL);
    }

    private void renderKeypad(final BlockState state,
                              final PoseStack poseStack,
                              final MultiBufferSource bufferSource,
                              final int packedLight,
                              final int packedOverlay) {

        final var renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        final var buffer = bufferSource.getBuffer(RenderType.solid());
        poseStack.pushPose();
        renderer.renderModel(poseStack.last(),
            buffer,
            state,
            keypadModel,
            1F,
            1F,
            1F,
            packedLight,
            packedOverlay,
            ModelData.EMPTY,
            RenderType.solid());
        poseStack.popPose();
    }

    private void onRenderBlockEntity(final BlockEntityRenderEvent event) {
        final var blockEntity = event.getBlockEntity();
        final var state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof KeypadChestBlock) || !(blockEntity instanceof KeypadChestBlockEntity chestBlockEntity) || !chestBlockEntity.isPrimaryChest()) {
            return;
        }
        renderKeypad(state,
            event.getPoseStack(),
            event.getBufferSource(),
            event.getPackedLight(),
            OverlayTexture.NO_OVERLAY);
    }

    private void onRenderBlockEntityItem(final BlockEntityItemRenderEvent event) {
        final var block = Block.byItem(event.getStack().getItem());
        if (!(block instanceof KeypadChestBlock)) {
            return;
        }
        renderKeypad(block.defaultBlockState(),
            event.getPoseStack(),
            event.getBufferSource(),
            event.getPackedLight(),
            event.getPackedOverlay());
    }
}
