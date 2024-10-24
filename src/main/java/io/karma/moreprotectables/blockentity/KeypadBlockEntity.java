package io.karma.moreprotectables.blockentity;

import net.geforcemods.securitycraft.api.*;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
public interface KeypadBlockEntity extends IPasscodeProtected, IOwnable, IModuleInventory, ICustomizable {
    Vec3i X_AXIS = new Vec3i(1, 0, 0);
    Vec3i Z_AXIS = new Vec3i(0, 0, 1);

    Map<ModuleType, Boolean> getModuleStates();

    Option.BooleanOption getSendAllowlistMessage();

    Option.BooleanOption getSendDenylistMessage();

    Option.SmartModuleCooldownOption getSmartModuleCooldown();

    @Nullable
    ResourceLocation getPreviousBlock();

    void setPreviousBlock(final @Nullable ResourceLocation previousBlock);

    void setInventory(final NonNullList<ItemStack> modules);

    void setCooldownEnd(final long cooldownEnd);

    default boolean sendsAllowlistMessage() {
        return getSendAllowlistMessage().get();
    }

    default boolean sendsDenylistMessage() {
        return getSendDenylistMessage().get();
    }

    boolean isOpen();

    default void addOrRemoveModuleFromAttached(final ItemStack module, final boolean remove, final boolean toggled) {
        if (!module.isEmpty()) {
            if (module.getItem() instanceof ModuleItem moduleItem) {
                final var offsetBe = (KeypadChestBlockEntity) findOtherBlock();
                if (offsetBe != null) {
                    if (toggled) {
                        if (offsetBe.isModuleEnabled(moduleItem.getModuleType()) != remove) {
                            return;
                        }
                    }
                    else if (offsetBe.hasModule(moduleItem.getModuleType()) != remove) {
                        return;
                    }

                    if (remove) {
                        offsetBe.removeModule(moduleItem.getModuleType(), toggled);
                    }
                    else {
                        offsetBe.insertModule(module, toggled);
                    }
                }
            }
        }
    }

    @Override
    default void onModuleInserted(final ItemStack stack, final ModuleType module, final boolean toggled) {
        IModuleInventory.super.onModuleInserted(stack, module, toggled);
        this.addOrRemoveModuleFromAttached(stack, false, toggled);
        if (module == ModuleType.DISGUISE) {
            DisguisableBlockEntity.onDisguiseModuleInserted(getThis(), stack, toggled);
        }
    }

    @Override
    default void onModuleRemoved(final ItemStack stack, final ModuleType module, final boolean toggled) {
        IModuleInventory.super.onModuleRemoved(stack, module, toggled);
        this.addOrRemoveModuleFromAttached(stack, true, toggled);
        if (module == ModuleType.DISGUISE) {
            DisguisableBlockEntity.onDisguiseModuleRemoved(getThis(), stack, toggled);
        }
    }

    @Override
    default <T> void onOptionChanged(final Option<T> option) {
        final var otherBe = (KeypadChestBlockEntity) findOtherBlock();
        if (otherBe != null) {
            if (option instanceof Option.BooleanOption boolOption) {
                if (option == getSendAllowlistMessage()) {
                    otherBe.getSendAllowlistMessage().setValue(boolOption.get());
                }
                else {
                    if (option != getSendDenylistMessage()) {
                        throw new UnsupportedOperationException("Unhandled option synchronization in keypad chest! " + option.getName());
                    }
                    otherBe.getSendAllowlistMessage().setValue(boolOption.get());
                }
            }
            else {
                if (!(option instanceof Option.IntOption) || option != getSmartModuleCooldown()) {
                    throw new UnsupportedOperationException("Unhandled option synchronization in keypad chest! " + option.getName());
                }

                otherBe.getSmartModuleCooldown().copy(option);
            }
        }
        ICustomizable.super.onOptionChanged(option);
    }

    @Override
    default void startCooldown() {
        final var otherHalf = (KeypadBlockEntity) findOtherBlock();
        final var start = System.currentTimeMillis();
        startCooldown(start);
        if (otherHalf != null) {
            otherHalf.startCooldown(start);
        }
    }

    default void startCooldown(final long start) {
        if (!isOnCooldown()) {
            setCooldownEnd(start + (long) (getSmartModuleCooldown().get() * 50));
            final var state = getThisState();
            Objects.requireNonNull(getThis().getLevel()).sendBlockUpdated(getThisPos(), state, state, 0x2);
            getThis().setChanged();
        }
    }

    default boolean isPrimaryBlock() {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    default boolean isModuleEnabled(final ModuleType module) {
        return hasModule(module) && getModuleStates().get(module) == Boolean.TRUE;
    }

    @Override
    default void toggleModuleState(final ModuleType module, final boolean shouldBeEnabled) {
        getModuleStates().put(module, shouldBeEnabled);
    }

    @Override
    default Level myLevel() {
        return getThis().getLevel();
    }

    @Override
    default BlockPos myPos() {
        return getThis().getBlockPos();
    }

    @Nullable
    default BlockEntity findOtherBlock() {
        return null;
    }

    default BlockEntity getThis() {
        return (BlockEntity) this;
    }

    default BlockPos getThisPos() {
        return getThis().getBlockPos();
    }

    default BlockState getThisState() {
        return getThis().getBlockState();
    }

    default void loadAdditionalKeypadData(final CompoundTag data) {
        setCooldownEnd(System.currentTimeMillis() + data.getLong("cooldownLeft"));
        loadSaltKey(data);
        loadPasscode(data);
        getOwner().load(data);
        setPreviousBlock(new ResourceLocation(data.getString("previous_block")));
    }

    default void saveAdditionalKeypadData(final CompoundTag data) {
        final var cooldownLeft = getCooldownEnd() - System.currentTimeMillis();
        data.putLong("cooldownLeft", cooldownLeft <= 0 ? -1 : cooldownLeft);
        final var saltKey = getSaltKey();
        if (saltKey != null) {
            data.putUUID("saltKey", saltKey);
        }
        final var passcode = getPasscode();
        if (passcode != null) {
            data.putString("passcode", PasscodeUtils.bytesToString(passcode));
        }
        getOwner().save(data, needsValidation());
        final var previousChest = getPreviousBlock();
        if (previousChest != null) {
            data.putString("previous_block", previousChest.toString());
        }
    }

    @Override
    default void activate(final Player player) {
    }

    @Override
    default boolean isOnCooldown() {
        return System.currentTimeMillis() < getCooldownEnd();
    }

    @Override
    default void openPasscodeGUI(final Level level, final BlockPos pos, final Player player) {
        if (!level.isClientSide) {
            IPasscodeProtected.super.openPasscodeGUI(level, pos, player);
        }
    }
}
