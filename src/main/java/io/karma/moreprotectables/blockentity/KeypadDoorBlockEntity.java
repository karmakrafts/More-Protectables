package io.karma.moreprotectables.blockentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public interface KeypadDoorBlockEntity extends KeypadBlockEntity {
    default void loadAdditionalDoorData(final CompoundTag tag) {
    }

    default void saveAdditionalDoorData(final CompoundTag tag) {
    }

    void setIsDisabled(final boolean disabled);

    boolean isDisabled();

    int getSignalLength();

    void setSignalLength(final int signalLength);

    @Override
    default boolean isPrimaryBlock() {
        final var state = getBEBlockState();
        if (!state.hasProperty(DoorBlock.HALF)) {
            return true;
        }
        return state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER;
    }
}
