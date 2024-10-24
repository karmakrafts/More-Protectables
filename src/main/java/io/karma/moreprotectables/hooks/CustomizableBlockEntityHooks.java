package io.karma.moreprotectables.hooks;

import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * @author Alexander Hinze
 * @since 24/10/2024
 */
public interface CustomizableBlockEntityHooks {
    void moreprotectables$setInventory(final NonNullList<ItemStack> modules);

    Map<ModuleType, Boolean> moreprotectables$getModuleStates();
}
