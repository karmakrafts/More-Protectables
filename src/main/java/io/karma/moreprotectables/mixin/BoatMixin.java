package io.karma.moreprotectables.mixin;

import io.karma.moreprotectables.hooks.BoatHooks;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Boat.Type;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Alexander Hinze
 * @since 20/10/2024
 */
@Mixin(Boat.class)
public abstract class BoatMixin extends Entity implements BoatHooks {
    @Shadow
    @Final
    public static EntityDataAccessor<Integer> DATA_ID_TYPE;

    public BoatMixin(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    @Override
    public void moreprotectables$setType(final Type type) {
        entityData.set(DATA_ID_TYPE, type.ordinal());
    }

    @Override
    public Type moreprotectables$getType() {
        return Type.values()[entityData.get(DATA_ID_TYPE)];
    }
}
