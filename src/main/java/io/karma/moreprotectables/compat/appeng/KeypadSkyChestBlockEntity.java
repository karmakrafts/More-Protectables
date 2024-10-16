package io.karma.moreprotectables.compat.appeng;

import appeng.block.storage.SkyChestBlock.SkyChestType;
import appeng.blockentity.storage.SkyChestBlockEntity;
import io.karma.moreprotectables.util.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.api.*;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 14/10/2024
 */
public final class KeypadSkyChestBlockEntity extends SkyChestBlockEntity
    implements KeypadChestBlockEntity, IPasscodeProtected, IOwnable, IModuleInventory, ICustomizable {
    private final Owner owner = new Owner();
    private final BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
    private final BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
    private final SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption();
    private byte[] passcode;
    private UUID saltKey;
    private NonNullList<ItemStack> modules = NonNullList.withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
    private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
    private ResourceLocation previousChest;
    private long cooldownEnd = 0;

    public KeypadSkyChestBlockEntity(final SkyChestType type, final BlockPos pos, final BlockState blockState) {
        super(getBlockEntityType(type), pos, blockState);
    }

    private static BlockEntityType<KeypadSkyChestBlockEntity> getBlockEntityType(final SkyChestType type) {
        if (type == SkyChestType.STONE) {
            return AppengCompatibilityContent.keypadSkyChestBlockEntity.get();
        }
        return AppengCompatibilityContent.keypadSmoothSkyChestBlockEntity.get();
    }

    @Override
    public void saveAdditional(final CompoundTag data) {
        super.saveAdditional(data);
        writeModuleInventory(data);
        writeModuleStates(data);
        writeOptions(data);
        final var cooldownLeft = getCooldownEnd() - System.currentTimeMillis();
        data.putLong("cooldownLeft", cooldownLeft <= 0 ? -1 : cooldownLeft);
        if (saltKey != null) {
            data.putUUID("saltKey", saltKey);
        }
        if (passcode != null) {
            data.putString("passcode", PasscodeUtils.bytesToString(passcode));
        }
        owner.save(data, needsValidation());
        if (previousChest != null) {
            data.putString("previous_chest", previousChest.toString());
        }
    }

    @Override
    public void loadTag(final CompoundTag data) {
        super.loadTag(data);
        modules = readModuleInventory(data);
        moduleStates = readModuleStates(data);
        readOptions(data);
        cooldownEnd = System.currentTimeMillis() + data.getLong("cooldownLeft");
        loadSaltKey(data);
        loadPasscode(data);
        owner.load(data);
        previousChest = new ResourceLocation(data.getString("previous_chest"));
    }

    @Override
    public Option<?>[] customOptions() {
        return new Option[]{sendAllowlistMessage, sendDenylistMessage, smartModuleCooldown};
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        return modules;
    }

    @Override
    public ModuleType[] acceptedModules() {
        return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.REDSTONE, ModuleType.SMART, ModuleType.HARMING, ModuleType.DISGUISE};
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isModuleEnabled(final ModuleType moduleType) {
        return hasModule(moduleType) && moduleStates.get(moduleType) == Boolean.TRUE;
    }

    @Override
    public void toggleModuleState(final ModuleType moduleType, final boolean shouldBeEnabled) {
        moduleStates.put(moduleType, shouldBeEnabled);
    }

    @Override
    public Level myLevel() {
        return level;
    }

    @Override
    public BlockPos myPos() {
        return worldPosition;
    }

    @Override
    public Owner getOwner() {
        return owner;
    }

    @Override
    public void setOwner(final String uuid, final String name) {
        owner.set(uuid, name);
        setChanged();
    }

    @Override
    public void activate(final Player player) {
        //if (!level.isClientSide && getBlockState().getBlock() instanceof KeypadSkyChestBlock block && !isBlocked()) {
        //    block.activate(getBlockState(), level, worldPosition, player);
        //}
    }

    @Override
    public byte[] getPasscode() {
        return passcode == null || passcode.length == 0 ? null : passcode;
    }

    @Override
    public void setPasscode(final byte[] passcode) {
        this.passcode = passcode;
        setChanged();
    }

    @Override
    public UUID getSaltKey() {
        return saltKey;
    }

    @Override
    public void setSaltKey(final UUID saltKey) {
        this.saltKey = saltKey;
    }

    @Override
    public void startCooldown() {
        final var start = System.currentTimeMillis();
        if (!isOnCooldown()) {
            cooldownEnd = start + smartModuleCooldown.get() * 50;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
            setChanged();
        }
    }

    @Override
    public boolean isOnCooldown() {
        return System.currentTimeMillis() < getCooldownEnd();
    }

    @Override
    public long getCooldownEnd() {
        return cooldownEnd;
    }

    //private boolean isBlocked() {
    //    for (final var dir : Direction.Plane.HORIZONTAL) {
    //        final var pos = getBlockPos().relative(dir);
    //
    //        if (level.getBlockState(pos).getBlock() instanceof KeypadSkyChestBlock && KeypadSkyChestBlock.isBlocked(
    //            level,
    //            pos))
    //            return true;
    //    }
    //    return KeypadSkyChestBlock.isBlocked(getLevel(), getBlockPos());
    //}

    @Nullable
    @Override
    public BlockEntity findOtherChest() {
        return null;
    }

    @Override
    public BlockPos getBEPos() {
        return worldPosition;
    }

    @Override
    public BlockState getBEBlockState() {
        return getBlockState();
    }
}