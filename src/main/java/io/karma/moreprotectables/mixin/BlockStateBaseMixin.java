package io.karma.moreprotectables.mixin;

import io.karma.moreprotectables.block.ReinforcedShadowBlock;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Alexander Hinze
 * @since 28/10/2024
 */
@Mixin(BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Shadow
    public abstract Block getBlock();

    @Inject(method = "is(Lnet/minecraft/world/level/block/Block;)Z", at = @At("HEAD"), cancellable = true)
    private void onIs(final Block block, final CallbackInfoReturnable<Boolean> cbi) {
        // @formatter:off
        if (getBlock() instanceof ReinforcedShadowBlock shadowBlock
            && shadowBlock.getVanillaBlock() == block) {
            cbi.setReturnValue(true);
            cbi.cancel();
        }
        // @formatter:on
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void onIs(final TagKey<Block> tag, final CallbackInfoReturnable<Boolean> cbi) {
        // @formatter:off
        if (getBlock() instanceof ReinforcedShadowBlock shadowBlock
            && shadowBlock.getVanillaBlock().builtInRegistryHolder().is(tag)) {
            cbi.setReturnValue(true);
            cbi.cancel();
        }
        // @formatter:on
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "is(Lnet/minecraft/core/HolderSet;)Z", at = @At("HEAD"), cancellable = true)
    private void onIs(final HolderSet<Block> holderSet, final CallbackInfoReturnable<Boolean> cbi) {
        if (getBlock() instanceof ReinforcedShadowBlock shadowBlock && holderSet.contains(shadowBlock.getVanillaBlock().builtInRegistryHolder())) {
            cbi.setReturnValue(true);
            cbi.cancel();
        }
    }
}
