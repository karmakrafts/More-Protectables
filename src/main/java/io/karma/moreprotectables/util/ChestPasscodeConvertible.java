package io.karma.moreprotectables.util;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public final class ChestPasscodeConvertible implements IPasscodeConvertible {
    private final Block unprotectedBlock;
    private final Block protectedBlock;

    public ChestPasscodeConvertible(final Block unprotectedBlock, final Block protectedBlock) {
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
        convert(player, level, pos, true);
        return true;
    }

    @Override
    public boolean unprotect(final Player player, final Level level, final BlockPos pos) {
        convert(player, level, pos, false);
        return true;
    }

    private void convert(final Player player, final Level level, final BlockPos pos, final boolean protect) {
        final var state = level.getBlockState(pos);
        final var facing = state.getValue(HorizontalDirectionalBlock.FACING);
        // @formatter:off
        final var type = state.hasProperty(ChestBlock.TYPE)
            ? state.getValue(ChestBlock.TYPE)
            : ChestType.SINGLE;
        // @formatter:on
        final var chest = level.getBlockEntity(pos);
        if (chest == null) {
            return;
        }

        if (!protect && chest instanceof IModuleInventory moduleInventory) {
            moduleInventory.dropAllModules();
        }

        convertSingleChest(chest, player, level, pos, state, facing, type, protect);

        if (type != ChestType.SINGLE) {
            final var newPos = pos.relative(ChestBlock.getConnectedDirection(state));
            final var newState = level.getBlockState(newPos);
            convertSingleChest(Objects.requireNonNull(level.getBlockEntity(newPos)),
                player,
                level,
                newPos,
                newState,
                facing,
                type.getOpposite(),
                protect);
        }
    }

    private void convertSingleChest(BlockEntity chest,
                                    final Player player,
                                    final Level level,
                                    final BlockPos pos,
                                    final BlockState oldChestState,
                                    final Direction facing,
                                    final ChestType type,
                                    final boolean protect) {
        CompoundTag tag;
        final var convertedBlock = protect ? protectedBlock : unprotectedBlock;

        tag = chest.saveWithFullMetadata();
        if (chest instanceof Container container) {
            container.clearContent();
        }

        if (chest instanceof ChestBlockEntity) {
            level.setBlockAndUpdate(pos,
                convertedBlock.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing).setValue(
                    ChestBlock.TYPE,
                    type));
        }
        else {
            level.setBlockAndUpdate(pos,
                convertedBlock.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing));
        }

        chest = level.getBlockEntity(pos);
        if (chest == null) {
            return;
        }
        chest.load(tag);

        if (protect) {
            if (player != null) {
                ((IOwnable) chest).setOwner(player.getUUID().toString(), player.getName().getString());
            }
            ((KeypadChestBlockEntity) chest).setPreviousChest(ForgeRegistries.BLOCKS.getKey(oldChestState.getBlock()));
        }
    }
}
