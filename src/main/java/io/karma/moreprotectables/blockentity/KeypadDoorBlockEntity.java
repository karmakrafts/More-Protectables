package io.karma.moreprotectables.blockentity;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public interface KeypadDoorBlockEntity extends KeypadBlockEntity, IModuleInventory {
    int getSignalLength();

    default void runForOtherHalf(final Consumer<BlockEntity> action) {
        final var level = getThis().getLevel();
        if (level != null) {
            BlockEntity be = null;
            if (getThisState().getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
                be = level.getBlockEntity(getThisPos().above());
            }
            else if (getThisState().getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
                be = level.getBlockEntity(getThisPos().below());
            }
            action.accept(be);
        }
    }

    @Override
    default boolean isOpen() {
        final var state = getThisState();
        return state.hasProperty(DoorBlock.OPEN) && state.getValue(DoorBlock.OPEN);
    }

    @Override
    default boolean isPrimaryBlock() {
        final var state = getThisState();
        if (!state.hasProperty(DoorBlock.HALF)) {
            return true;
        }
        return state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER;
    }

    @Override
    default ModuleType[] acceptedModules() {
        return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.SMART, ModuleType.HARMING, ModuleType.DISGUISE};
    }

    @Override
    default boolean shouldAttemptCodebreak(Player player) {
        if (isDisabled()) {
            player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
            return false;
        }
        else {
            return !(Boolean) getThisState().getValue(DoorBlock.OPEN) && KeypadBlockEntity.super.shouldAttemptCodebreak(
                player);
        }
    }

    @Override
    default void startCooldown() {
        final var start = System.currentTimeMillis();
        startCooldown(start);
        runForOtherHalf(otherHalf -> ((KeypadDoorBlockEntity) otherHalf).startCooldown(start));
    }
}
