package io.karma.moreprotectables.mixin;

import io.karma.moreprotectables.hook.NeighborUpdaterHooks;
import net.minecraft.world.level.redstone.NeighborUpdater;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Alexander Hinze
 * @since 01/11/2024
 */
@Mixin(NeighborUpdater.class)
public interface NeighborUpdaterMixin extends NeighborUpdaterHooks {
    @Override
    default int moreprotectables$getMaxChainedNeighborUpdates() {
        return 1;
    }
}
