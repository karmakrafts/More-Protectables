package io.karma.moreprotectables.compat.tropicraft;

import io.karma.moreprotectables.block.KeypadDoorBlock;
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

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
public final class KeypadBambooDoorBlock extends SpecialDoorBlock implements KeypadDoorBlock {
    public KeypadBambooDoorBlock(final Properties properties, final BlockSetType type) {
        super(properties, type);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        return TropicraftCompatibilityContent.keypadBambooDoorBlockEntity.get().create(pos, state);
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
