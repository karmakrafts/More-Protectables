package io.karma.moreprotectables.compat.appeng;

import appeng.block.storage.SkyChestBlock.SkyChestType;
import appeng.blockentity.storage.SkyChestBlockEntity;
import io.karma.moreprotectables.blockentity.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 14/10/2024
 */
public final class KeypadSkyChestBlockEntity extends SkyChestBlockEntity implements KeypadChestBlockEntity {
    private final Owner owner = new Owner();
    private final BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
    private final BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
    private final SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption();
    private final Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
    private NonNullList<ItemStack> modules = NonNullList.withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
    private byte[] passcode;
    private UUID saltKey;
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
    public @NotNull CompoundTag getUpdateTag() {
        return PasscodeUtils.filterPasscodeAndSaltFromTag(this.saveWithoutMetadata());
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(final Connection net, final ClientboundBlockEntityDataPacket packet) {
        super.onDataPacket(net, packet);
        this.handleUpdateTag(packet.getTag());
    }

    @Override
    public void handleUpdateTag(final CompoundTag tag) {
        super.handleUpdateTag(tag);
        DisguisableBlockEntity.onHandleUpdateTag(this);
    }

    @Override
    public void saveAdditional(final CompoundTag data) {
        super.saveAdditional(data);
        saveAdditionalKeypadData(data);
    }

    @Override
    public void loadTag(final CompoundTag data) {
        super.loadTag(data);
        loadAdditionalKeypadData(data);
    }

    @Override
    public void setModuleStates(final Map<ModuleType, Boolean> states) {
        moduleStates.clear();
        moduleStates.putAll(states);
    }

    @Override
    public void setModules(final NonNullList<ItemStack> modules) {
        this.modules = modules;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public Option<?>[] customOptions() {
        return new Option[]{sendAllowlistMessage, sendDenylistMessage, smartModuleCooldown};
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        return modules;
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
    public @Nullable ResourceLocation getPreviousBlock() {
        return previousChest;
    }

    @Override
    public void setPreviousBlock(final @Nullable ResourceLocation previousBlock) {
        this.previousChest = previousBlock;
    }

    @Override
    public void startCooldown() {
        if (!hasLevel()) {
            return;
        }
        final var start = System.currentTimeMillis();
        if (!isOnCooldown()) {
            cooldownEnd = start + smartModuleCooldown.get() * 50;
            Objects.requireNonNull(level).sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
            setChanged();
        }
    }

    @Override
    public long getCooldownEnd() {
        return cooldownEnd;
    }

    @Override
    public void setCooldownEnd(final long cooldownEnd) {
        this.cooldownEnd = cooldownEnd;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        DisguisableBlockEntity.onSetRemoved(this);
    }

    @Override
    public @NotNull ModelData getModelData() {
        return DisguisableBlockEntity.getModelData(this);
    }

    @Override
    public boolean sendsAllowlistMessage() {
        return sendAllowlistMessage.get();
    }

    @Override
    public boolean sendsDenylistMessage() {
        return sendDenylistMessage.get();
    }

    @Override
    public @Nullable BlockEntity findOtherBlock() {
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