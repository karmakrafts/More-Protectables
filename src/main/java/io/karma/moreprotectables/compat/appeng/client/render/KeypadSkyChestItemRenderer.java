package io.karma.moreprotectables.compat.appeng.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.moreprotectables.compat.appeng.AppengCompatibilityContent;
import io.karma.moreprotectables.compat.appeng.KeypadSkyChestBlockEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 15/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class KeypadSkyChestItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final Lazy<KeypadSkyChestBlockEntity> SKY_CHEST_BLOCK_ENTITY = Lazy.of(() -> AppengCompatibilityContent.keypadSkyChestBlockEntity.get().create(
        BlockPos.ZERO,
        AppengCompatibilityContent.keypadSkyChest.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING,
            Direction.SOUTH)));
    private static final Lazy<KeypadSkyChestBlockEntity> SMOOTH_SKY_CHEST_BLOCK_ENTITY = Lazy.of(() -> AppengCompatibilityContent.keypadSmoothSkyChestBlockEntity.get().create(
        BlockPos.ZERO,
        AppengCompatibilityContent.keypadSmoothSkyChest.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING,
            Direction.SOUTH)));
    private final BlockEntityRenderDispatcher dispatcher;

    public KeypadSkyChestItemRenderer(final BlockEntityRenderDispatcher dispatcher,
                                      final EntityModelSet entityModelSet) {
        super(dispatcher, entityModelSet);
        this.dispatcher = dispatcher;
    }

    @Override
    public void renderByItem(final @NotNull ItemStack stack,
                             final @NotNull ItemDisplayContext displayContext,
                             final @NotNull PoseStack poseStack,
                             final @NotNull MultiBufferSource buffer,
                             final int packedLight,
                             final int packedOverlay) {
        final var item = stack.getItem();
        KeypadSkyChestBlockEntity blockEntity;
        if (item == AppengCompatibilityContent.keypadSkyChest.get().asItem()) {
            blockEntity = SKY_CHEST_BLOCK_ENTITY.get();
        }
        else {
            blockEntity = SMOOTH_SKY_CHEST_BLOCK_ENTITY.get();
        }
        final var renderer = dispatcher.getRenderer(blockEntity);
        if (renderer == null) {
            return;
        }
        poseStack.pushPose();
        renderer.render(blockEntity, 0F, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
