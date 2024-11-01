package io.karma.moreprotectables.blockentity;

import io.karma.moreprotectables.hook.CustomizableBlockEntityHooks;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 25/10/2024
 */
public class SimpleKeypadTrapdoorBlockEntity extends DisguisableBlockEntity implements KeypadTrapdoorBlockEntity {
    private final Option.BooleanOption sendAllowlistMessage = new Option.SendAllowlistMessageOption(false);
    private final Option.BooleanOption sendDenylistMessage = new Option.SendDenylistMessageOption(true);
    private final Option.IntOption signalLength = new Option.IntOption("signalLength", 60, 0, 400, 5);
    private final Option.DisabledOption disabled = new Option.DisabledOption(false);
    private final Option.SmartModuleCooldownOption smartModuleCooldown = new Option.SmartModuleCooldownOption();
    private long cooldownEnd = 0L;
    private byte[] passcode;
    private UUID saltKey;
    private ResourceLocation previousBlock;

    public SimpleKeypadTrapdoorBlockEntity(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Map<ModuleType, Boolean> getModuleStates() {
        return ((CustomizableBlockEntityHooks) this).moreprotectables$getModuleStates();
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
    public @Nullable ResourceLocation getPreviousBlock() {
        return previousBlock;
    }

    @Override
    public void setPreviousBlock(final @Nullable ResourceLocation previousBlock) {
        this.previousBlock = previousBlock;
        setChanged();
    }

    @Override
    public void setInventory(final NonNullList<ItemStack> modules) {
        ((CustomizableBlockEntityHooks) this).moreprotectables$setInventory(modules);
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
    public long getCooldownEnd() {
        return cooldownEnd;
    }

    @Override
    public void setCooldownEnd(final long cooldownEnd) {
        this.cooldownEnd = cooldownEnd;
        setChanged();
    }

    @Override
    public Option<?>[] customOptions() {
        return new Option[]{sendAllowlistMessage, sendDenylistMessage, signalLength, disabled, smartModuleCooldown};
    }
}
