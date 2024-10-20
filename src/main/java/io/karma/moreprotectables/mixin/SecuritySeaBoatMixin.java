package io.karma.moreprotectables.mixin;

import io.karma.moreprotectables.hooks.SecuritySeaBoatData;
import io.karma.moreprotectables.hooks.SecuritySeaBoatHooks;
import net.geforcemods.securitycraft.entity.SecuritySeaBoat;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Alexander Hinze
 * @since 21/10/2024
 */
@Mixin(SecuritySeaBoat.class)
public final class SecuritySeaBoatMixin extends ChestBoat implements SecuritySeaBoatHooks {
    public SecuritySeaBoatMixin(final EntityType<? extends Boat> type, final Level level) {
        super(type, level);
    }

    @SuppressWarnings("all")
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onClInit(final CallbackInfo cbi) {
        SecuritySeaBoatData.dataIdUsingPlayers = SynchedEntityData.defineId(SecuritySeaBoat.class,
            EntityDataSerializers.INT);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void onDefineSynchedData(final CallbackInfo cbi) {
        entityData.define(SecuritySeaBoatData.dataIdUsingPlayers, 0);
    }

    @Override
    public void startOpen(final @NotNull Player player) {
        super.startOpen(player);
        final var count = entityData.get(SecuritySeaBoatData.dataIdUsingPlayers);
        entityData.set(SecuritySeaBoatData.dataIdUsingPlayers, count + 1);
    }

    @Override
    public void stopOpen(final @NotNull Player player) {
        final var count = entityData.get(SecuritySeaBoatData.dataIdUsingPlayers);
        entityData.set(SecuritySeaBoatData.dataIdUsingPlayers, Math.max(0, count - 1));
        super.stopOpen(player);
    }

    @Override
    public boolean moreprotectables$isOpen() {
        return entityData.get(SecuritySeaBoatData.dataIdUsingPlayers) > 0;
    }
}
