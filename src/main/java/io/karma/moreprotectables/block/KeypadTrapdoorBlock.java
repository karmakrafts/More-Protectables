package io.karma.moreprotectables.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.TransformationHelper;

/**
 * @author Alexander Hinze
 * @since 25/10/2024
 */
public interface KeypadTrapdoorBlock extends KeypadBlock {
    @OnlyIn(Dist.CLIENT)
    @Override
    default void applyKeypadTransform(final PoseStack poseStack,
                                      final BlockState state,
                                      final boolean isItem,
                                      final float angle) {
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(TransformationHelper.quatFromXYZ(90F, 0F, 0F, true));
        poseStack.mulPose(TransformationHelper.quatFromXYZ(0F, 0F, 180F + angle, true));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        poseStack.translate(-(2F / 16F), 2F / 16F, 12F / 16F);
    }
}
