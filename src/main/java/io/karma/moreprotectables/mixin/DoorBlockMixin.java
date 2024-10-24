package io.karma.moreprotectables.mixin;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Alexander Hinze
 * @since 24/10/2024
 */
@Mixin(DoorBlock.class)
public final class DoorBlockMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(final BlockState state,
                       final Level level,
                       final BlockPos pos,
                       final Player player,
                       final InteractionHand hand,
                       final BlockHitResult hit,
                       final CallbackInfoReturnable<InteractionResult> cbi) {
        final var stack = player.getItemInHand(hand);
        if (stack.getItem() == SCContent.KEY_PANEL.get()) {
            cbi.setReturnValue(InteractionResult.CONSUME);
            cbi.cancel();
        }
    }
}
