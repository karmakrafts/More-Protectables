package io.karma.moreprotectables.compat.ironchest.mixin;

import com.progwml6.ironchest.common.item.ChestUpgradeItem;
import com.progwml6.ironchest.common.item.IronChestsUpgradeType;
import io.karma.moreprotectables.compat.ironchest.hooks.ChestUpgradeItemHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Alexander Hinze
 * @since 20/10/2024
 */
@Mixin(ChestUpgradeItem.class)
public class ChestUpgradeItemMixin implements ChestUpgradeItemHooks {
    @Shadow
    @Final
    private IronChestsUpgradeType type;

    @Override
    public IronChestsUpgradeType moreprotectables$getType() {
        return type;
    }
}
