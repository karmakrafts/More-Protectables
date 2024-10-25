package io.karma.moreprotectables.util;

import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author Alexander Hinze
 * @since 25/10/2024
 */
public final class KeypadTrapdoorConvertible implements IPasscodeConvertible {
    private final Block unprotectedBlock;
    private final Block protectedBlock;

    public KeypadTrapdoorConvertible(final Block unprotectedBlock, final Block protectedBlock) {
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
        return false;
    }

    @Override
    public boolean unprotect(final Player player, final Level level, final BlockPos pos) {
        return false;
    }
}
