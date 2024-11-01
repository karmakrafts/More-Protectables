package io.karma.moreprotectables.mixin;

import io.karma.moreprotectables.hook.NeighborUpdaterHooks;
import net.minecraft.world.level.redstone.CollectingNeighborUpdater;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Alexander Hinze
 * @since 01/11/2024
 */
@Mixin(CollectingNeighborUpdater.class)
public final class CollectingNeighborUpdaterMixin implements NeighborUpdaterHooks {
    @Shadow
    @Final
    private int maxChainedNeighborUpdates;

    @Override
    public int moreprotectables$getMaxChainedNeighborUpdates() {
        return maxChainedNeighborUpdates;
    }
}
