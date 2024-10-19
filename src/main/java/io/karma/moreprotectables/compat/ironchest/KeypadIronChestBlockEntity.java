package io.karma.moreprotectables.compat.ironchest;

import com.progwml6.ironchest.common.block.IronChestsTypes;
import com.progwml6.ironchest.common.block.regular.entity.AbstractIronChestBlockEntity;
import com.progwml6.ironchest.common.inventory.IronChestMenu;
import io.karma.moreprotectables.blockentity.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 15/10/2024
 */
public class KeypadIronChestBlockEntity extends AbstractIronChestBlockEntity implements KeypadChestBlockEntity {
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

    public KeypadIronChestBlockEntity(final IronChestsTypes type,
                                      final Supplier<Block> block,
                                      final BlockPos pos,
                                      final BlockState state) {
        super(IronChestCompatibilityContent.getKeypadChestBlockEntityType(type), pos, state, type, block);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(final int containerId,
                                                        final @NotNull Inventory playerInventory) {
        return switch (getChestType()) {
            case COPPER -> IronChestMenu.createCopperContainer(containerId, playerInventory, this);
            case GOLD -> IronChestMenu.createGoldContainer(containerId, playerInventory, this);
            case DIAMOND -> IronChestMenu.createDiamondContainer(containerId, playerInventory, this);
            case CRYSTAL -> IronChestMenu.createCrystalContainer(containerId, playerInventory, this);
            case OBSIDIAN -> IronChestMenu.createObsidianContainer(containerId, playerInventory, this);
            default -> IronChestMenu.createIronContainer(containerId, playerInventory, this);
        };
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
