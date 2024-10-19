package io.karma.moreprotectables.blockentity;

import io.karma.moreprotectables.block.KeypadDoorBlock;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blockentities.SpecialDoorBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
 * @since 19/10/2024
 */
public class SimpleKeypadDoorBlockEntity extends SpecialDoorBlockEntity implements KeypadDoorBlockEntity {
    protected final Option.IntOption signalLength = new Option.IntOption("signalLength",
        defaultSignalLength(),
        0,
        400,
        5);
    protected final Option.DisabledOption disabled = new Option.DisabledOption(false);
    private final Owner owner = new Owner();
    private final BooleanOption sendAllowlistMessage = new SendAllowlistMessageOption(false);
    private final BooleanOption sendDenylistMessage = new SendDenylistMessageOption(true);
    private final SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption();
    private final Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
    private NonNullList<ItemStack> modules = NonNullList.withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
    private byte[] passcode;
    private UUID saltKey;
    private ResourceLocation previousBlock;
    private long cooldownEnd = 0;

    public SimpleKeypadDoorBlockEntity(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    public static void tick(final Level level,
                            final BlockPos pos,
                            final BlockState state,
                            final BlockEntity blockEntity) {
        if (!(blockEntity instanceof ITickingBlockEntity tickingBlockEntity)) {
            return;
        }
        tickingBlockEntity.tick(level, pos, state);
    }

    @Override
    public void activate(final Player player) {
        if (hasLevel() && !Objects.requireNonNull(level).isClientSide) {
            final var state = getBlockState();
            if (state.getBlock() instanceof KeypadDoorBlock doorBlock) {
                doorBlock.activate(state, level, worldPosition, player);
            }
        }
    }

    @Override
    public int defaultSignalLength() {
        return 60;
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
    public void saveAdditional(final @NotNull CompoundTag data) {
        super.saveAdditional(data);
        saveAdditionalKeypadData(data);
    }

    @Override
    public void load(final @NotNull CompoundTag data) {
        super.load(data);
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
        return new Option[]{sendAllowlistMessage, sendDenylistMessage, signalLength, disabled, smartModuleCooldown};
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
        return previousBlock;
    }

    @Override
    public void setPreviousBlock(final @Nullable ResourceLocation previousBlock) {
        this.previousBlock = previousBlock;
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
    public void handleUpdateTag(final CompoundTag tag) {
        super.handleUpdateTag(tag);
        DisguisableBlockEntity.onHandleUpdateTag(this);
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