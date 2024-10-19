package io.karma.moreprotectables.client.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.geforcemods.securitycraft.renderers.SecuritySeaBoatRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

/**
 * @author Alexander Hinze
 * @since 20/10/2024
 */
@Mixin(SecuritySeaBoatRenderer.class)
public abstract class SecuritySeaBoatRendererMixin extends BoatRenderer {
    public SecuritySeaBoatRendererMixin(final Context context, final boolean isChestBoat) {
        super(context, isChestBoat);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(final Context context, final CallbackInfo cbi) {
        boatResources = Stream.of(Type.values()).collect(ImmutableMap.toImmutableMap((type) -> type,
            (type) -> Pair.of(new ResourceLocation("minecraft",
                "textures/entity/chest_boat/" + type.getName() + ".png"), createBoatModel(context, type, true))));
    }
}
