package io.karma.moreprotectables.blockentity;

import io.karma.moreprotectables.block.KeypadTrapdoorBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.TrapDoorBlock;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 25/10/2024
 */
public interface KeypadTrapdoorBlockEntity extends KeypadBlockEntity {
    @Override
    default ModuleType[] acceptedModules() {
        return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.SMART, ModuleType.HARMING, ModuleType.DISGUISE};
    }

    @Override
    default void activate(final Player player) {
        final var level = Objects.requireNonNull(getThis().getLevel());
        if (!level.isClientSide) {
            if (getThisState().getBlock() instanceof KeypadTrapdoorBlock trapdoorBlock) {
                trapdoorBlock.activate(getThisState(), level, getThisPos(), player, getSignalLength());
            }
        }
    }

    @Override
    default boolean shouldAttemptCodebreak(Player player) {
        if (isDisabled()) {
            player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
            return false;
        }
        else {
            return !(Boolean) getThisState().getValue(TrapDoorBlock.OPEN) && KeypadBlockEntity.super.shouldAttemptCodebreak(
                player);
        }
    }

    @Override
    default boolean isOpen() {
        return getThisState().getValue(TrapDoorBlock.OPEN);
    }
}
