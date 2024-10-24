package io.karma.moreprotectables.compat.tropicraft;

import io.karma.moreprotectables.blockentity.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.tropicraft.core.common.block.tileentity.BambooChestBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public final class KeypadBambooChestBlockEntity extends BambooChestBlockEntity implements KeypadChestBlockEntity {
    private final Owner owner = new Owner();
    private final Option.BooleanOption sendAllowlistMessage = new Option.SendAllowlistMessageOption(false);
    private final Option.BooleanOption sendDenylistMessage = new Option.SendAllowlistMessageOption(true);
    private final Option.SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption();
    private byte[] passcode;
    private UUID saltKey;
    private NonNullList<ItemStack> modules = NonNullList.withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
    private long cooldownEnd;
    private Map<ModuleType, Boolean> moduleStates;
    private ResourceLocation previousChest;

    public KeypadBambooChestBlockEntity(final BlockEntityType<BambooChestBlockEntity> type,
                                        final BlockPos pos,
                                        final BlockState state) {
        super(type, pos, state);
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
    public void saveAdditional(final @NotNull CompoundTag data) {
        super.saveAdditional(data);
        saveAdditionalKeypadData(data);
        writeModuleInventory(data);
        writeModuleStates(data);
        writeOptions(data);
    }

    @Override
    public void load(final @NotNull CompoundTag data) {
        super.load(data);
        loadAdditionalKeypadData(data);
        modules = readModuleInventory(data);
        moduleStates = readModuleStates(data);
        readOptions(data);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return getBlockState().getBlock().getName();
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
    public long getCooldownEnd() {
        return cooldownEnd;
    }

    @Override
    public void setCooldownEnd(final long cooldownEnd) {
        this.cooldownEnd = cooldownEnd;
        setChanged();
    }

    @Override
    public BooleanOption getSendAllowlistMessage() {
        return sendAllowlistMessage;
    }

    @Override
    public BooleanOption getSendDenylistMessage() {
        return sendDenylistMessage;
    }

    @Override
    public SmartModuleCooldownOption getSmartModuleCooldown() {
        return smartModuleCooldown;
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
    public void setInventory(final NonNullList<ItemStack> modules) {
        this.modules = modules;
    }

    @Override
    public Map<ModuleType, Boolean> getModuleStates() {
        return moduleStates;
    }
}
