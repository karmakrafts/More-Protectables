package io.karma.moreprotectables.blockentity;

import io.karma.moreprotectables.hooks.CustomizableBlockEntityHooks;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blockentities.SpecialDoorBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public class SimpleKeypadDoorBlockEntity extends SpecialDoorBlockEntity implements KeypadDoorBlockEntity {
    private final Option.BooleanOption sendAllowlistMessage = new Option.SendAllowlistMessageOption(false);
    private final Option.BooleanOption sendDenylistMessage = new Option.SendAllowlistMessageOption(true);
    private final Option.SmartModuleCooldownOption smartModuleCooldown = new SmartModuleCooldownOption();
    private byte[] passcode;
    private UUID saltKey;
    private long cooldownEnd;
    private ResourceLocation previousBlock;

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
    public int defaultSignalLength() {
        return 60;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return getBlockState().getBlock().getName();
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
        setChanged();
    }

    @Override
    public @Nullable ResourceLocation getPreviousBlock() {
        return previousBlock;
    }

    @Override
    public void setPreviousBlock(final @Nullable ResourceLocation previousBlock) {
        this.previousBlock = previousBlock;
        setChanged();
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
    public void setInventory(final NonNullList<ItemStack> modules) {
        ((CustomizableBlockEntityHooks) this).moreprotectables$setInventory(modules);
        setChanged();
    }

    @Override
    public Map<ModuleType, Boolean> getModuleStates() {
        return ((CustomizableBlockEntityHooks) this).moreprotectables$getModuleStates();
    }
}
