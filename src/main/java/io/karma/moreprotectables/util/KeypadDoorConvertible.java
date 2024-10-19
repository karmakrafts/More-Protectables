package io.karma.moreprotectables.util;

import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public final class KeypadDoorConvertible implements IPasscodeConvertible {
    private final Block unprotectedBlock;
    private final Block protectedBlock;

    public KeypadDoorConvertible(final Block unprotectedBlock, final Block protectedBlock) {
        this.unprotectedBlock = unprotectedBlock;
        this.protectedBlock = protectedBlock;
    }

    @Override
    public boolean isUnprotectedBlock(final BlockState state) {
        return state.is(unprotectedBlock);
    }

    @Override
    public boolean isProtectedBlock(final BlockState state) {
        return state.is(protectedBlock);
    }

    @Override
    public boolean protect(final Player player, final Level level, final BlockPos pos) {
        final var state = level.getBlockState(pos);
        convert(level, pos, true);
        if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            convert(level, pos.above(), true);
            return true;
        }
        convert(level, pos.below(), true);
        return true;
    }

    @Override
    public boolean unprotect(final Player player, final Level level, final BlockPos pos) {
        final var state = level.getBlockState(pos);
        convert(level, pos, false);
        if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            convert(level, pos.above(), false);
            return true;
        }
        convert(level, pos.below(), false);
        return true;
    }

    private void convert(final Level level, final BlockPos pos, final boolean protect) {
        final var state = level.getBlockState(pos);
        final var half = state.getValue(DoorBlock.HALF);
        final var facing = state.getValue(DoorBlock.FACING);
        final var open = state.getValue(DoorBlock.OPEN);
        // @formatter:off
        level.setBlock(pos, (protect ? protectedBlock.defaultBlockState() : unprotectedBlock.defaultBlockState())
            .setValue(DoorBlock.HALF, half)
            .setValue(DoorBlock.FACING, facing)
            .setValue(DoorBlock.OPEN, open), 0x3);
        // @formatter:on
    }
}
