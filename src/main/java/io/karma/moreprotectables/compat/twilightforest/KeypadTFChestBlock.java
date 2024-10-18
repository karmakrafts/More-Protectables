package io.karma.moreprotectables.compat.twilightforest;

import io.karma.moreprotectables.util.KeypadChestBlock;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
public final class KeypadTFChestBlock extends ChestBlock implements KeypadChestBlock {
    public KeypadTFChestBlock(final Properties properties,
                              final Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityType) {
        super(properties, blockEntityType);
    }

    @Override
    public BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        return blockEntityType.get().create(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(final @NotNull BlockState state,
                                          final @NotNull Level level,
                                          final @NotNull BlockPos pos,
                                          final @NotNull Player player,
                                          final @NotNull InteractionHand hand,
                                          final @NotNull BlockHitResult hit) {
        return useChest(state, level, pos, player, hand, hit);
    }

    @Override
    public @NotNull VoxelShape getShape(final @NotNull BlockState state,
                                        final @NotNull BlockGetter level,
                                        final @NotNull BlockPos pos,
                                        final @NotNull CollisionContext context) {
        final var disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);
        if (disguisedState.getBlock() != this) {
            return disguisedState.getShape(level, pos, context);
        }
        return super.getShape(state, level, pos, context);
    }

    @Override
    public int getLightEmission(final BlockState state, final BlockGetter level, final BlockPos pos) {
        final var disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);
        if (disguisedState.getBlock() != this) {
            return disguisedState.getLightEmission(level, pos);
        }
        return super.getLightEmission(state, level, pos);
    }

    @Override
    public SoundType getSoundType(final BlockState state,
                                  final LevelReader level,
                                  final BlockPos pos,
                                  final Entity entity) {
        final var disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);
        if (disguisedState.getBlock() != this) {
            return disguisedState.getSoundType(level, pos, entity);
        }
        return super.getSoundType(state, level, pos, entity);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getShadeBrightness(final @NotNull BlockState state,
                                    final @NotNull BlockGetter level,
                                    final @NotNull BlockPos pos) {
        final var disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);
        if (disguisedState.getBlock() != this) {
            return disguisedState.getShadeBrightness(level, pos);
        }
        return super.getShadeBrightness(state, level, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightBlock(final @NotNull BlockState state,
                             final @NotNull BlockGetter level,
                             final @NotNull BlockPos pos) {
        final var disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);
        if (disguisedState.getBlock() != this) {
            return disguisedState.getLightBlock(level, pos);
        }
        return super.getLightBlock(state, level, pos);
    }

    @Override
    public BlockState getAppearance(final BlockState state,
                                    final BlockAndTintGetter level,
                                    final BlockPos pos,
                                    final Direction side,
                                    final BlockState queryState,
                                    final BlockPos queryPos) {
        return IDisguisable.getDisguisedStateOrDefault(state, level, pos);
    }

    @Override
    public ItemStack getDisplayStack(Level level, BlockState state, BlockPos pos) {
        return getDisguisedStack(level, pos);
    }

    @Override
    public boolean shouldShowSCInfo(Level level, BlockState state, BlockPos pos) {
        return getDisguisedStack(level, pos).getItem() == asItem();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state,
                                       HitResult target,
                                       BlockGetter level,
                                       BlockPos pos,
                                       Player player) {
        return getDisguisedStack(level, pos);
    }

    @Override
    public void setPlacedBy(final @NotNull Level level,
                            final @NotNull BlockPos pos,
                            final @NotNull BlockState state,
                            final @NotNull LivingEntity entity,
                            final @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (entity instanceof Player player) {
            MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(final @NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}