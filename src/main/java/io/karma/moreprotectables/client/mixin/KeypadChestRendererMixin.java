package io.karma.moreprotectables.client.mixin;

import net.geforcemods.securitycraft.renderers.KeypadChestRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
@Mixin(value = KeypadChestRenderer.class)
public class KeypadChestRendererMixin<T extends ChestBlockEntity> extends ChestRenderer<T> {
    public KeypadChestRendererMixin(final Context context) {
        super(context);
    }

    @Inject(method = "getMaterial(Lnet/minecraft/world/level/block/entity/ChestBlockEntity;Lnet/minecraft/world/level/block/state/properties/ChestType;)Lnet/minecraft/client/resources/model/Material;", at = @At("HEAD"), cancellable = true, remap = false)
    private void onGetMaterial(final T blockEntity, final ChestType type, final CallbackInfoReturnable<Material> cbi) {
        cbi.setReturnValue(super.getMaterial(blockEntity, type));
        cbi.cancel();
    }
}
