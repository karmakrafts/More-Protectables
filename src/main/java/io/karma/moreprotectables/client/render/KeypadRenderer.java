package io.karma.moreprotectables.client.render;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.block.KeypadBlock;
import io.karma.moreprotectables.blockentity.KeypadBlockEntity;
import io.karma.moreprotectables.client.event.BlockEntityRenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.TransformationHelper;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * @author Alexander Hinze
 * @since 14/10/2024
 */
public final class KeypadRenderer {
    public static final KeypadRenderer INSTANCE = new KeypadRenderer();
    public static final ResourceLocation KEYPAD_MODEL = new ResourceLocation(MoreProtectables.MODID,
        "block/keypad_on_block");
    public static final ResourceLocation KEYPAD_UNLOCKED_MODEL = new ResourceLocation(MoreProtectables.MODID,
        "block/keypad_on_block_unlocked");
    public static final ResourceLocation KEYPAD_LOCKED_MODEL = new ResourceLocation(MoreProtectables.MODID,
        "block/keypad_on_block_locked");

    private BakedModel keypadModel;
    private BakedModel keypadUnlockedModel;
    private BakedModel keypadLockedModel;

    // @formatter:off
    private KeypadRenderer() {}
    // @formatter:on

    @Internal
    public void setup() {
        final var bus = MinecraftForge.EVENT_BUS;
        final var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onRegisterAdditionalModels);
        modBus.addListener(this::onBakingComplete);
        bus.addListener(this::onRenderBlockEntity);
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

    private void onRenderBlockEntity(final BlockEntityRenderEvent event) {
        final var blockEntity = event.getBlockEntity();
        final var state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof KeypadBlock keypadBlock) || !(blockEntity instanceof KeypadBlockEntity keypadBlockEntity) || !keypadBlockEntity.isPrimaryBlock()) {
            return;
        }
        final var renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        final var buffer = event.getBufferSource().getBuffer(RenderType.cutout());
        final var poseStack = event.getPoseStack();
        final var offset = keypadBlock.getKeypadOffset(state);
        final var rotationOffset = keypadBlock.getKeypadRotationOffset(state);
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        if (event.isItem()) {
            poseStack.mulPose(TransformationHelper.quatFromXYZ(0F, 180F + rotationOffset, 0F, true));
        }
        else {
            final var facing = state.getValue(HorizontalDirectionalBlock.FACING);
            final var angle = facing.getAxis() == Axis.Z ? facing.getOpposite().toYRot() : facing.toYRot();
            poseStack.mulPose(TransformationHelper.quatFromXYZ(0F, angle + rotationOffset, 0F, true));
        }
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        poseStack.translate(-offset.x, offset.y, -offset.z);
        final var isLocked = event.isItem() || !keypadBlockEntity.isOpen();
        final var buttonModel = isLocked ? keypadLockedModel : keypadUnlockedModel;
        renderer.renderModel(poseStack.last(),
            buffer,
            state,
            keypadModel,
            1F,
            1F,
            1F,
            event.getPackedLight(),
            event.getPackedOverlay(),
            ModelData.EMPTY,
            RenderType.cutout());
        renderer.renderModel(poseStack.last(),
            buffer,
            state,
            buttonModel,
            1F,
            1F,
            1F,
            LightTexture.FULL_BRIGHT,
            event.getPackedOverlay(),
            ModelData.EMPTY,
            RenderType.cutout());
        poseStack.popPose();
    }
}
