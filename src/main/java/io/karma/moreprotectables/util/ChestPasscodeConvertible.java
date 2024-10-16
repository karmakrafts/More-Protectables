package io.karma.moreprotectables.util;

import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
    public boolean protect(Player player, Level level, BlockPos pos) {
        //convert(player, level, pos, true);
        return true;
    }

    @Override
    public boolean unprotect(Player player, Level level, BlockPos pos) {
        //convert(player, level, pos, false);
        return true;
    }

    //private void convert(Player player, Level level, BlockPos pos, boolean protect) {
    //    BlockState state = level.getBlockState(pos);
    //    Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
    //    BlockEntity chest = level.getBlockEntity(pos);
    //
    //    if (!protect)
    //        ((IModuleInventory) chest).dropAllModules();
    //
    //    convertSingleChest(chest, player, level, pos, state, facing, type, protect);
    //
    //    if (type != ChestType.SINGLE) {
    //        BlockPos newPos = pos.relative(getConnectedDirection(state));
    //        BlockState newState = level.getBlockState(newPos);
    //
    //        convertSingleChest((ChestBlockEntity) level.getBlockEntity(newPos), player, level, newPos, newState, facing, type.getOpposite(), protect);
    //    }
    //}
    //
    //private void convertSingleChest(ChestBlockEntity chest, Player player, Level level, BlockPos pos, BlockState oldChestState, Direction facing, ChestType type, boolean protect) {
    //    CompoundTag tag;
    //    Block convertedBlock;
    //
    //    if (protect)
    //        convertedBlock = SCContent.KEYPAD_CHEST.get();
    //    else {
    //        convertedBlock = BuiltInRegistries.BLOCK.get(((KeypadChestBlockEntity) chest).getPreviousChest());
    //
    //        if (convertedBlock == Blocks.AIR)
    //            convertedBlock = Blocks.CHEST;
    //    }
    //
    //    chest.unpackLootTable(player); //generate loot (if any), so items don't spill out when converting and no additional loot table is generated
    //    tag = chest.saveWithFullMetadata();
    //    chest.clearContent();
    //    level.setBlockAndUpdate(pos, convertedBlock.defaultBlockState().setValue(FACING, facing).setValue(TYPE, type));
    //    chest = (ChestBlockEntity) level.getBlockEntity(pos);
    //    chest.load(tag);
    //
    //    if (protect) {
    //        if (player != null)
    //            ((IOwnable) chest).setOwner(player.getUUID().toString(), player.getName().getString());
    //
    //        ((KeypadChestBlockEntity) chest).setPreviousChest(oldChestState.getBlock());
    //    }
    //}
}
