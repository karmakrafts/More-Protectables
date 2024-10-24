package io.karma.moreprotectables.mixin;

import io.karma.moreprotectables.hooks.CustomizableBlockEntityHooks;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

/**
 * @author Alexander Hinze
 * @since 24/10/2024
 */
@Mixin(CustomizableBlockEntity.class)
public final class CustomizableBlockEntityMixin implements CustomizableBlockEntityHooks {
    @Shadow
    private Map<ModuleType, Boolean> moduleStates;

    @Shadow
    private NonNullList<ItemStack> modules;

    @Override
    public void moreprotectables$setInventory(final NonNullList<ItemStack> modules) {
        this.modules = modules;
    }

    @Override
    public Map<ModuleType, Boolean> moreprotectables$getModuleStates() {
        return moduleStates;
    }
}
