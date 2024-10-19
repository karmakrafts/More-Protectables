package io.karma.moreprotectables.blockentity;

import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public interface KeypadDoorBlockEntity extends KeypadBlockEntity {
    boolean isDisabled();

    int getSignalLength();

    @Override
    default boolean isOpen() {
        final var state = getBEBlockState();
        return state.hasProperty(DoorBlock.OPEN) && state.getValue(DoorBlock.OPEN);
    }

    @Override
    default boolean isPrimaryBlock() {
        final var state = getBEBlockState();
        if (!state.hasProperty(DoorBlock.HALF)) {
            return true;
        }
        return state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER;
    }

    @Override
    default ModuleType[] acceptedModules() {
        return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.SMART, ModuleType.HARMING, ModuleType.DISGUISE};
    }
}
