package io.karma.moreprotectables.mixin;

import io.karma.moreprotectables.hooks.BoatHooks;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.SecuritySeaBoat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Alexander Hinze
 * @since 20/10/2024
 */
@Mixin(ChestBoat.class)
public abstract class ChestBoatMixin extends Boat implements BoatHooks {
    public ChestBoatMixin(final EntityType<? extends Boat> type, final Level level) {
        super(type, level);
    }

    @SuppressWarnings("all")
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void onInteract(final Player player,
                            final InteractionHand hand,
                            final CallbackInfoReturnable<InteractionResult> cbi) {
        if (!(ChestBoat.class.cast(this) instanceof SecuritySeaBoat)) {
            final var stack = player.getItemInHand(hand);
            if (stack.getItem() == SCContent.KEY_PANEL.get()) {
                final var level = level();
                if (!level.isClientSide) {
                    final var serverLevel = (ServerLevel) level;
                    final var newEntity = SCContent.SECURITY_SEA_BOAT_ENTITY.get().create(serverLevel);
                    newEntity.copyPosition(this);
                    serverLevel.addFreshEntity(newEntity);
                    remove(RemovalReason.DISCARDED);
                }
                cbi.setReturnValue(InteractionResult.CONSUME);
                cbi.cancel();
            }
        }
    }
}
