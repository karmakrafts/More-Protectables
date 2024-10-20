package io.karma.moreprotectables.hooks;

import net.geforcemods.securitycraft.entity.SecuritySeaBoat;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

/**
 * @author Alexander Hinze
 * @since 20/10/2024
 */
public interface SecuritySeaBoatHooks {
    EntityDataAccessor<Integer> DATA_ID_USING_PLAYER_COUNT = SynchedEntityData.defineId(SecuritySeaBoat.class,
        EntityDataSerializers.INT);

    int moreprotectables$getUsingPlayerCount();
}
