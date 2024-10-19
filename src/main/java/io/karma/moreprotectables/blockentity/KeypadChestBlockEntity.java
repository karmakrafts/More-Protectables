package io.karma.moreprotectables.blockentity;

import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public interface KeypadChestBlockEntity extends KeypadBlockEntity, LidBlockEntity {
    @Override
    default boolean isOpen() {
        return getOpenNess(0F) > 0F;
    }

    @Override
    default boolean isPrimaryBlock() {
        final var other = findOtherBlock();
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

    @Override
    default @Nullable BlockEntity findOtherBlock() {
        final var state = getBEBlockState();
        final var type = state.getValue(ChestBlock.TYPE);
        if (type != ChestType.SINGLE) {
            final var offsetPos = getBEPos().relative(ChestBlock.getConnectedDirection(state));
            final var level = Objects.requireNonNull(getThisBlockEntity().getLevel());
            final var offsetState = level.getBlockState(offsetPos);
            if (state.getBlock() == offsetState.getBlock()) {
                final var offsetType = offsetState.getValue(ChestBlock.TYPE);
                if (offsetType != ChestType.SINGLE && type != offsetType && state.getValue(ChestBlock.FACING) == offsetState.getValue(
                    ChestBlock.FACING)) {
                    return level.getBlockEntity(offsetPos);
                }
            }
        }
        return null;
    }
}
