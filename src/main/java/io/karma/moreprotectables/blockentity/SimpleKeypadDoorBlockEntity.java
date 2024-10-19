package io.karma.moreprotectables.blockentity;

import net.geforcemods.securitycraft.api.Option;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public class SimpleKeypadDoorBlockEntity extends SimpleKeypadBlockEntity implements KeypadDoorBlockEntity {
    protected final Option.IntOption signalLength = new Option.IntOption("signalLength", 60, 0, 400, 5);
    protected final Option.DisabledOption disabled = new Option.DisabledOption(false);

    public SimpleKeypadDoorBlockEntity(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void load(final @NotNull CompoundTag data) {
        super.load(data);
        loadAdditionalDoorData(data);
    }

    @Override
    public void saveAdditional(final @NotNull CompoundTag data) {
        super.saveAdditional(data);
        saveAdditionalDoorData(data);
    }

    @Override
    public void setIsDisabled(final boolean disabled) {
        this.disabled.setValue(disabled);
    }

    @Override
    public boolean isDisabled() {
        return disabled.get();
    }

    @Override
    public int getSignalLength() {
        return signalLength.get();
    }

    @Override
    public void setSignalLength(final int signalLength) {
        this.signalLength.setValue(signalLength);
    }
}
