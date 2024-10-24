package io.karma.moreprotectables.util;

import io.karma.moreprotectables.blockentity.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.registries.ForgeRegistries;

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
        convert(player, level, pos, true);
        if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            convert(player, level, pos.above(), true);
            LinkableBlockEntity.unlink((LinkableBlockEntity) level.getBlockEntity(pos),
                (LinkableBlockEntity) level.getBlockEntity(pos.above()));
            return true;
        }
        convert(player, level, pos.below(), true);
        LinkableBlockEntity.unlink((LinkableBlockEntity) level.getBlockEntity(pos),
            (LinkableBlockEntity) level.getBlockEntity(pos.below()));
        return true;
    }

    @Override
    public boolean unprotect(final Player player, final Level level, final BlockPos pos) {
        final var state = level.getBlockState(pos);
        convert(player, level, pos, false);
        if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            convert(player, level, pos.above(), false);
            LinkableBlockEntity.unlink((LinkableBlockEntity) level.getBlockEntity(pos),
                (LinkableBlockEntity) level.getBlockEntity(pos.above()));
            return true;
        }
        convert(player, level, pos.below(), false);
        LinkableBlockEntity.unlink((LinkableBlockEntity) level.getBlockEntity(pos),
            (LinkableBlockEntity) level.getBlockEntity(pos.below()));
        return true;
    }

    private void convert(final Player player, final Level level, final BlockPos pos, final boolean protect) {
        final var state = level.getBlockState(pos);
        final var half = state.getValue(DoorBlock.HALF);
        final var facing = state.getValue(DoorBlock.FACING);
        final var open = state.getValue(DoorBlock.OPEN);
        final var hinge = state.getValue(DoorBlock.HINGE);
        // @formatter:off
        level.setBlockAndUpdate(pos, (protect ? protectedBlock.defaultBlockState() : unprotectedBlock.defaultBlockState())
            .setValue(DoorBlock.HALF, half)
            .setValue(DoorBlock.FACING, facing)
            .setValue(DoorBlock.OPEN, open)
            .setValue(DoorBlock.HINGE, hinge));
        // @formatter:on
        if (protect) {
            final var blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof KeypadDoorBlockEntity doorBlockEntity)) {
                return;
            }
            doorBlockEntity.setPreviousBlock(ForgeRegistries.BLOCKS.getKey(state.getBlock()));
            doorBlockEntity.setOwner(player.getUUID().toString(), player.getName().getString());
        }
    }
}
