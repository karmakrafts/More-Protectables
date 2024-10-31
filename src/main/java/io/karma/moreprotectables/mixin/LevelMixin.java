package io.karma.moreprotectables.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.BlockSnapshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author Alexander Hinze
 * @since 31/10/2024
 */
@Mixin(Level.class)
public final class LevelMixin {
    @SuppressWarnings("all")
    @Inject( // @formatter:off
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
            shift = Shift.AFTER
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILSOFT
    ) // @formatter:on
    private void onSetBlock(final BlockPos pos,
                            final BlockState state,
                            final int flags,
                            final int recursionLeft,
                            final CallbackInfoReturnable<BlockState> cbi,
                            final LevelChunk levelChunk,
                            final Block block,
                            final BlockSnapshot blockSnapshot,
                            final BlockState oldState) {

    }
}
