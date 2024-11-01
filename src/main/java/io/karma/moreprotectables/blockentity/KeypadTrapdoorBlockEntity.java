package io.karma.moreprotectables.blockentity;

import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.TrapDoorBlock;

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
