package io.karma.moreprotectables.mixin;

import io.karma.moreprotectables.hooks.SecuritySeaBoatHooks;
import net.geforcemods.securitycraft.entity.SecuritySeaBoat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Alexander Hinze
 * @since 20/10/2024
 */
@Mixin(SecuritySeaBoat.class)
public final class SecuritySeaBoatMixin extends ChestBoat implements SecuritySeaBoatHooks {
    public SecuritySeaBoatMixin(final EntityType<? extends Boat> type, final Level level) {
        super(type, level);
    }

    @Unique
    private void moreprotectables$setUsingPlayerCount(final int usingPlayerCount) {
        entityData.set(DATA_ID_USING_PLAYER_COUNT, usingPlayerCount);
    }

    @Override
    public int moreprotectables$getUsingPlayerCount() {
        return entityData.get(DATA_ID_USING_PLAYER_COUNT);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void onDefinedSynchedData(final CallbackInfo cbi) {
        entityData.define(DATA_ID_USING_PLAYER_COUNT, 0);
    }

    @Inject(method = "openCustomInventoryScreen", at = @At("TAIL"))
    private void onOpenCustomInventoryScreen(final Player player, final CallbackInfo cbi) {
        final var level = player.level();
        if (!level.isClientSide) {
            final var count = moreprotectables$getUsingPlayerCount();
            moreprotectables$setUsingPlayerCount(count + 1);
        }
    }

    @Override
    public void stopOpen(final @NotNull Player player) {
        final var level = player.level();
        if (!level.isClientSide) {
            final var count = moreprotectables$getUsingPlayerCount();
            moreprotectables$setUsingPlayerCount(Math.max(0, count - 1));
        }
        super.stopOpen(player);
    }
}
