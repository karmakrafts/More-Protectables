package io.karma.moreprotectables.client.mixin;

import io.karma.moreprotectables.client.model.ModelPatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * @author Alexander Hinze
 * @since 31/10/2024
 */
@Mixin(ModelBakery.class)
public final class ModelBakeryMixin {
    @Shadow
    @Final
    public static ModelResourceLocation MISSING_MODEL_LOCATION;
    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void onGetModel(final ResourceLocation location, final CallbackInfoReturnable<UnbakedModel> cbi) {
        if (ModelPatcher.INSTANCE.maySkipLoad(location)) {
            cbi.setReturnValue(unbakedCache.get(MISSING_MODEL_LOCATION));
            cbi.cancel();
        }
    }

    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private void onLoadModel(final ResourceLocation location, final CallbackInfo cbi) {
        if (ModelPatcher.INSTANCE.maySkipLoad(location)) {
            cbi.cancel();
        }
    }
}
