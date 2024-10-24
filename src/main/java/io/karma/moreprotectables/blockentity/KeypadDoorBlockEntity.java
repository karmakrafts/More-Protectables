package io.karma.moreprotectables.blockentity;

import io.karma.moreprotectables.block.KeypadDoorBlock;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public interface KeypadDoorBlockEntity extends KeypadBlockEntity, IModuleInventory {
    boolean isDisabled();

    int getSignalLength();

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
    default void activate(final Player player) {
        final var level = Objects.requireNonNull(getThis().getLevel());
        if (!level.isClientSide) {
            final var state = getThisState();
            if (state.getBlock() instanceof KeypadDoorBlock doorBlock) {
                doorBlock.activate(state, level, getThisPos(), player);
            }
        }
    }
}
