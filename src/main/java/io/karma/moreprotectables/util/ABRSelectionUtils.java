package io.karma.moreprotectables.util;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 02/11/2024
 */
public final class ABRSelectionUtils {
    // @formatter:off
    private ABRSelectionUtils() {}
    // @formatter:on

    private static void findConnectedBlocks(final BlockPos pos,
                                            final BlockGetter level,
                                            final int radius,
                                            final int depth,
                                            final BlockState commonState,
                                            final LongOpenHashSet visited,
                                            final Consumer<BlockPos> callback) {
        final var currentState = level.getBlockState(pos);
        var packedPosition = pos.asLong();
        if (!currentState.is(commonState.getBlock())) {
            return;
        }
        if (!visited.contains(packedPosition)) {
            callback.accept(pos);
            visited.add(packedPosition);
        }
        if (depth == radius) {
            return;
        }
        final var offsetPos = new MutableBlockPos();
        for (final var direction : Direction.values()) {
            offsetPos.set(pos);
            offsetPos.move(direction);
            findConnectedBlocks(offsetPos, level, radius, depth + 1, commonState, visited, callback);
        }
    }

    public static void findConnectedBlocks(final BlockPos pos,
                                           final BlockGetter level,
                                           final int radius,
                                           final Consumer<BlockPos> callback) {
        findConnectedBlocks(pos, level, radius, 0, level.getBlockState(pos), new LongOpenHashSet(), callback);
    }
}
