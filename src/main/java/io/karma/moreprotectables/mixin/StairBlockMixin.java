package io.karma.moreprotectables.mixin;

import io.karma.moreprotectables.block.ReinforcedShadowBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Alexander Hinze
 * @since 28/10/2024
 */
@Mixin(StairBlock.class)
public final class StairBlockMixin {
    @Inject(method = "isStairs", at = @At("HEAD"), cancellable = true)
    private static void onIsStairs(final BlockState state, final CallbackInfoReturnable<Boolean> cbi) {
        if (state.getBlock() instanceof ReinforcedShadowBlock shadowBlock) {
            cbi.setReturnValue(shadowBlock.getVanillaBlock() instanceof StairBlock);
            cbi.cancel();
        }
    }
}
