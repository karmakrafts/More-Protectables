package io.karma.moreprotectables.block;

import net.geforcemods.securitycraft.api.*;
import net.geforcemods.securitycraft.misc.SaltData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public class KeypadWoodDoorBlock extends DoorBlock implements KeypadDoorBlock {
    private final Supplier<BlockEntityType<?>> blockEntityType;

    public KeypadWoodDoorBlock(final Properties properties,
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
    public void neighborChanged(final @NotNull BlockState state,
                                final @NotNull Level level,
                                final @NotNull BlockPos pos,
                                final @NotNull Block block,
                                final @NotNull BlockPos fromPos,
                                boolean isMoving) {
    }

    @Override
    public void setPlacedBy(final @NotNull Level level,
                            final @NotNull BlockPos pos,
                            final @NotNull BlockState state,
                            final @NotNull LivingEntity placer,
                            final @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity var8 = level.getBlockEntity(pos);
        if (var8 instanceof IOwnable lowerBe) {
            var8 = level.getBlockEntity(pos.above());
            if (var8 instanceof IOwnable upperBe) {
                if (placer instanceof Player player) {
                    lowerBe.setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
                    upperBe.setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
                }

                if (lowerBe instanceof LinkableBlockEntity linkable1) {
                    if (upperBe instanceof LinkableBlockEntity linkable2) {
                        LinkableBlockEntity.link(linkable1, linkable2);
                    }
                }

                if (stack.hasCustomHoverName() && lowerBe instanceof INameSetter nameSetter1) {
                    if (upperBe instanceof INameSetter nameSetter2) {
                        nameSetter1.setCustomName(stack.getHoverName());
                        nameSetter2.setCustomName(stack.getHoverName());
                    }
                }
            }
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(final @NotNull BlockState state,
                     final @NotNull ServerLevel level,
                     @NotNull BlockPos upperPos,
                     final @NotNull RandomSource rand) {
        BlockState upperState = level.getBlockState(upperPos);
        if (upperState.getValue(DoorBlock.OPEN)) {
            BlockPos lowerPos;
            BlockState lowerState;
            if (upperState.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
                lowerPos = upperPos;
                lowerState = upperState;
                upperPos = upperPos.above();
                upperState = level.getBlockState(upperPos);
            }
            else {
                lowerPos = upperPos.below();
                lowerState = level.getBlockState(lowerPos);
            }

            level.setBlockAndUpdate(upperPos, upperState.setValue(DoorBlock.OPEN, false));
            level.setBlockAndUpdate(lowerPos, lowerState.setValue(DoorBlock.OPEN, false));
            level.playSound((Entity) null,
                upperPos,
                type().doorClose(),
                SoundSource.BLOCKS,
                1.0F,
                level.getRandom().nextFloat() * 0.1F + 0.9F);
            level.gameEvent(null, GameEvent.BLOCK_CLOSE, upperPos);
        }
    }

    @Override
    public void playerWillDestroy(final @NotNull Level level,
                                  final @NotNull BlockPos pos,
                                  final @NotNull BlockState state,
                                  final @NotNull Player player) {
        if (player.isCreative()) {
            BlockEntity var6 = level.getBlockEntity(pos);
            if (var6 instanceof IModuleInventory inv) {
                inv.getInventory().clear();
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState getStateForPlacement(final @NotNull BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        return state == null ? null : state.setValue(OPEN, false).setValue(POWERED, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(final @NotNull BlockState state,
                         final @NotNull Level level,
                         final @NotNull BlockPos pos,
                         final @NotNull BlockState newState,
                         boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IModuleInventory inv) {
                if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    inv.dropAllModules();
                }
            }

            if (be instanceof IPasscodeProtected passcodeProtected) {
                SaltData.removeSalt(passcodeProtected.getSaltKey());
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean triggerEvent(final @NotNull BlockState state,
                                final @NotNull Level level,
                                final @NotNull BlockPos pos,
                                int id,
                                int param) {
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && be.triggerEvent(id, param);
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
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        BlockState disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);
        return disguisedState.getBlock() != this ? disguisedState.getLightEmission(level, pos) : super.getLightEmission(
            state,
            level,
            pos);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        BlockState disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);
        return disguisedState.getBlock() != this ? disguisedState.getSoundType(level, pos, entity) : super.getSoundType(
            state,
            level,
            pos,
            entity);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getShadeBrightness(final @NotNull BlockState state,
                                    final @NotNull BlockGetter level,
                                    final @NotNull BlockPos pos) {
        BlockState disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);
        return disguisedState.getBlock() != this ? disguisedState.getShadeBrightness(level,
            pos) : super.getShadeBrightness(state, level, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightBlock(final @NotNull BlockState state,
                             final @NotNull BlockGetter level,
                             final @NotNull BlockPos pos) {
        BlockState disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);
        return disguisedState.getBlock() != this ? disguisedState.getLightBlock(level, pos) : super.getLightBlock(state,
            level,
            pos);
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
    public ItemStack getDisplayStack(final Level level, final BlockState state, final BlockPos pos) {
        return getDisguisedStack(level, pos);
    }

    @Override
    public boolean shouldShowSCInfo(final Level level, final BlockState state, final BlockPos pos) {
        return getDisguisedStack(level, pos).getItem() == asItem();
    }

    @Override
    public ItemStack getDefaultStack() {
        return new ItemStack(asItem());
    }
}
