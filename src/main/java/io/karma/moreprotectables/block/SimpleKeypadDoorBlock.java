package io.karma.moreprotectables.block;

import io.karma.moreprotectables.blockentity.SimpleKeypadDoorBlockEntity;
import net.geforcemods.securitycraft.blocks.SpecialDoorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public class SimpleKeypadDoorBlock extends SpecialDoorBlock implements KeypadDoorBlock {
    private final Supplier<BlockEntityType<?>> blockEntityType;

    public SimpleKeypadDoorBlock(final Properties properties,
                                 final BlockSetType type,
                                 final Supplier<BlockEntityType<?>> blockEntityType) {
        super(properties, type);
        this.blockEntityType = blockEntityType;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        return blockEntityType.get().create(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(final @NotNull BlockState state,
                                          final @NotNull Level level,
                                          final @NotNull BlockPos pos,
                                          final @NotNull Player player,
                                          final @NotNull InteractionHand hand,
                                          final @NotNull BlockHitResult hit) {
        return useDoor(state, level, pos, player, hand, hit);
    }

    @Override
    public Item getDoorItem() {
        return asItem();
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final @NotNull Level level,
                                                                  final @NotNull BlockState state,
                                                                  final @NotNull BlockEntityType<T> type) {
        return SimpleKeypadDoorBlockEntity::tick;
    }
}
