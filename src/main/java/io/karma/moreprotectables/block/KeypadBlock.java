package io.karma.moreprotectables.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
public interface KeypadBlock extends EntityBlock {
    @OnlyIn(Dist.CLIENT)
    default void applyKeypadTransform(final PoseStack poseStack,
                                      final BlockState state,
                                      final boolean isItem,
                                      final float angle) {
    }

    default Block getThisBlock() {
        return (Block) this;
    }

    String getDescriptionId();

    void activate(final BlockState state,
                  final Level level,
                  final BlockPos pos,
                  final Player player,
                  final int signalLength);
}
