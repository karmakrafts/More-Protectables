package io.karma.moreprotectables;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * @author Alexander Hinze
 * @since 20/10/2024
 */
public final class EventHandler {
    public static final EventHandler INSTANCE = new EventHandler();

    // @formatter:off
    private EventHandler() {}
    // @formatter:on

    @Internal
    public void setup() {
        final var bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::onEntityInteract);
    }

    private void onEntityInteract(final PlayerInteractEvent.EntityInteract event) {
        final var entity = event.getTarget();
        if (entity.getType() != EntityType.CHEST_BOAT) {
            return;
        }
        final var stack = event.getItemStack();
        if (stack.getItem() != SCContent.KEY_PANEL.get()) {
            return;
        }
        final var level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }
        final var newEntity = SCContent.SECURITY_SEA_BOAT_ENTITY.get().create(level);
        if (newEntity == null) {
            return;
        }
        final var entityData = entity.getEntityData().getNonDefaultValues();
        if (entityData != null) {
            newEntity.getEntityData().assignValues(entityData); // Make sure we retain the boat type
        }
        newEntity.setOwner(event.getEntity()); // Make sure we are the owner after the conversion
        newEntity.copyPosition(entity);
        level.addFreshEntity(newEntity);
        entity.remove(RemovalReason.DISCARDED);
        event.setCanceled(true); // We don't want to sit in the boat in this case
    }
}
