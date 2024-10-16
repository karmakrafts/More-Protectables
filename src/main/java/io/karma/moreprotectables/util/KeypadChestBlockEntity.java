package io.karma.moreprotectables.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public interface KeypadChestBlockEntity {
    Vec3i X_AXIS = new Vec3i(1, 0, 0);
    Vec3i Z_AXIS = new Vec3i(0, 0, 1);

    @Nullable
    BlockEntity findOtherChest();

    BlockPos getBEPos();

    BlockState getBEBlockState();

    default boolean isPrimaryChest() {
        final var other = findOtherChest();
        if (other == null) {
            return true;
        }
        final var pos = getBEPos();
        final var state = getBEBlockState();
        final var facing = state.getValue(HorizontalDirectionalBlock.FACING);
        final var otherPos = other.getBlockPos();
        // @formatter:off
        if ((facing.getAxis() == Axis.X && facing.getAxisDirection() == AxisDirection.NEGATIVE)
            || (facing.getAxis() == Axis.Z && facing.getAxisDirection() == AxisDirection.POSITIVE)) {
            return otherPos.equals(pos.offset(X_AXIS)) || otherPos.equals(pos.offset(Z_AXIS));
        }
        // @formatter:on
        return otherPos.equals(pos.subtract(X_AXIS)) || otherPos.equals(pos.subtract(Z_AXIS));
    }
}
