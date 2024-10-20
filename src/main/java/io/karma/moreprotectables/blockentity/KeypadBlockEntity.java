package io.karma.moreprotectables.blockentity;

import io.karma.moreprotectables.block.KeypadChestBlock;
import net.geforcemods.securitycraft.api.*;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
public interface KeypadBlockEntity extends IPasscodeProtected, IOwnable, IModuleInventory, ICustomizable, ILockable {
    Vec3i X_AXIS = new Vec3i(1, 0, 0);
    Vec3i Z_AXIS = new Vec3i(0, 0, 1);

    @NotNull
    Component getDisplayName();

    @Nullable
    ResourceLocation getPreviousBlock();

    void setPreviousBlock(final @Nullable ResourceLocation previousBlock);

    NonNullList<ItemStack> getModules();

    void setModules(final NonNullList<ItemStack> modules);

    void setModuleStates(final Map<ModuleType, Boolean> states);

    void setCooldownEnd(final long cooldownEnd);

    boolean sendsAllowlistMessage();

    boolean sendsDenylistMessage();

    boolean isOpen();

    default void clearModules() {
        getModules().clear();
    }

    default boolean isPrimaryBlock() {
        return true;
    }

    @Nullable
    default BlockEntity findOtherBlock() {
        return null;
    }

    BlockPos getBEPos();

    BlockState getBEBlockState();

    default void loadAdditionalKeypadData(final CompoundTag data) {
        setModules(readModuleInventory(data));
        setModuleStates(readModuleStates(data));
        readOptions(data);
        setCooldownEnd(System.currentTimeMillis() + data.getLong("cooldownLeft"));
        loadSaltKey(data);
        loadPasscode(data);
        getOwner().load(data);
        setPreviousBlock(new ResourceLocation(data.getString("previous_chest")));
    }

    default void saveAdditionalKeypadData(final CompoundTag data) {
        writeModuleInventory(data);
        writeModuleStates(data);
        writeOptions(data);
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
            data.putString("previous_chest", previousChest.toString());
        }
    }

    default boolean isBlocked() {
        final var blockEntity = getThisBlockEntity();
        if (!blockEntity.hasLevel()) {
            return true;
        }
        final var level = Objects.requireNonNull(blockEntity.getLevel());
        for (final var dir : Direction.Plane.HORIZONTAL) {
            final var pos = blockEntity.getBlockPos().relative(dir);
            if (level.getBlockState(pos).getBlock() == blockEntity.getBlockState().getBlock() && net.geforcemods.securitycraft.blocks.KeypadChestBlock.isBlocked(
                level,
                pos)) {
                return true;
            }
        }
        return net.geforcemods.securitycraft.blocks.KeypadChestBlock.isBlocked(Objects.requireNonNull(level),
            blockEntity.getBlockPos());
    }

    @Override
    default ModuleType[] acceptedModules() {
        return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.REDSTONE, ModuleType.SMART, ModuleType.HARMING, ModuleType.DISGUISE};
    }

    @Override
    default void activate(final Player player) {
        final var blockEntity = getThisBlockEntity();
        if (!blockEntity.hasLevel()) {
            return;
        }
        final var level = Objects.requireNonNull(blockEntity.getLevel());
        if (!level.isClientSide && blockEntity.getBlockState().getBlock() instanceof KeypadChestBlock block && !isBlocked()) {
            block.activate(blockEntity.getBlockState(), level, blockEntity.getBlockPos(), player);
        }
    }

    @Override
    default boolean isOnCooldown() {
        return System.currentTimeMillis() < getCooldownEnd();
    }

    @Override
    default void openPasscodeGUI(final Level level, final BlockPos pos, final Player player) {
        if (!level.isClientSide && !isBlocked()) {
            IPasscodeProtected.super.openPasscodeGUI(level, pos, player);
        }
    }

    @Override
    default void dropAllModules() {
        final var blockEntity = getThisBlockEntity();
        if (!blockEntity.hasLevel()) {
            return;
        }
        for (final var module : getInventory()) {
            if (!(module.getItem() instanceof ModuleItem)) {
                continue;
            }
            Block.popResource(Objects.requireNonNull(blockEntity.getLevel()), blockEntity.getBlockPos(), module);
        }
        getInventory().clear();
    }

    @Override
    default void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
        IModuleInventory.super.onModuleInserted(stack, module, toggled);
        if (module == ModuleType.DISGUISE) {
            DisguisableBlockEntity.onDisguiseModuleInserted(getThisBlockEntity(), stack, toggled);
        }
    }

    @Override
    default void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
        IModuleInventory.super.onModuleRemoved(stack, module, toggled);
        if (module == ModuleType.DISGUISE) {
            DisguisableBlockEntity.onDisguiseModuleRemoved(getThisBlockEntity(), stack, toggled);
        }
    }
}
