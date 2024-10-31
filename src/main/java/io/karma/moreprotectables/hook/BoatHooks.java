package io.karma.moreprotectables.hook;

import net.minecraft.world.entity.vehicle.Boat;

/**
 * @author Alexander Hinze
 * @since 20/10/2024
 */
public interface BoatHooks {
    void moreprotectables$setType(final Boat.Type type);

    Boat.Type moreprotectables$getType();
}
