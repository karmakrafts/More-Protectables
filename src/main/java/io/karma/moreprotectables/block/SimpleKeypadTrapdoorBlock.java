package io.karma.moreprotectables.block;

import io.karma.moreprotectables.blockentity.KeypadTrapdoorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 25/10/2024
 */
public class SimpleKeypadTrapdoorBlock extends TrapDoorBlock implements KeypadTrapdoorBlock {
    private final Supplier<BlockEntityType<? extends KeypadTrapdoorBlockEntity>> blockEntityType;

    public SimpleKeypadTrapdoorBlock(final Properties properties,
                                     final BlockSetType blockSetType,
                                     final Supplier<BlockEntityType<? extends KeypadTrapdoorBlockEntity>> blockEntityType) {
        super(properties, blockSetType);
        this.blockEntityType = blockEntityType;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        return blockEntityType.get().create(pos, state);
    }

    @Override
    public void activate(final BlockState state, final Level level, final BlockPos pos, final Player player) {

    }
}
