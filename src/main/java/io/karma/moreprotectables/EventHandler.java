package io.karma.moreprotectables;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 20/10/2024
 */
public final class EventHandler {
    public static final EventHandler INSTANCE = new EventHandler();

    // @formatter:off
    private EventHandler() {}
    // @formatter:on

    @SuppressWarnings("all")
    private static @Nullable BlockPos clipReinforcedBlock(final Level level,
                                                          final Entity entity,
                                                          final Vec3 start,
                                                          final Vec3 end) {
        return BlockGetter.traverseBlocks(start, end, null, ($, pos) -> {
            final var state = level.getBlockState(pos);
            final var shape = state.getCollisionShape(level, pos);
            final var hit = level.clipWithInteractionOverride(start, end, pos, shape, state);
            if (hit == null || hit.getType() != Type.BLOCK) {
                return null;
            }
            final var blockHit = (BlockHitResult) hit;
            final var hitPos = blockHit.getBlockPos();
            final var hitState = level.getBlockState(hitPos);
            if (!(hitState.getBlock() instanceof IReinforcedBlock)) {
                return null;
            }
            return hitPos;
        }, $ -> null);
    }

    @Internal
    public void init() {
        final var bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::onEntityInteract);
        bus.addListener(EventPriority.HIGHEST, this::onEntityTeleport);
    }

    private void onEntityTeleport(final EntityTeleportEvent event) {
        final var entity = event.getEntity();
        final var level = entity.level();
        if (level.isClientSide) {
            return;
        }
        final var start = event.getPrev();
        final var end = event.getTarget();
        final var pos = clipReinforcedBlock(level, entity, start, end);
        // Players with op level >= 1 may bypass teleportation protection
        // TODO: make required bypass perm level configurable
        if (pos == null || (entity instanceof Player player && player.hasPermissions(1))) {
            return;
        }
        final var blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof IOwnable ownable)) {
            return;
        }
        if (ownable.isOwnedBy(entity)) {
            return;
        }
        if (entity instanceof Player player) {
            player.sendSystemMessage(Component.translatable(String.format("message.%s.teleport_blocked",
                MoreProtectables.MODID)));
        }
        event.setCanceled(true);
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
