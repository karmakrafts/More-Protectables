package io.karma.moreprotectables.compat.appeng;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseEntityBlock;
import appeng.block.storage.SkyChestBlock.SkyChestType;
import appeng.blockentity.storage.SkyChestBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.menu.MenuOpener;
import appeng.menu.implementations.SkyChestMenu;
import appeng.menu.locator.MenuLocators;
import io.karma.moreprotectables.util.KeypadChestBlock;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 14/10/2024
 */
public final class KeypadSkyChestBlock extends AEBaseEntityBlock<KeypadSkyChestBlockEntity>
    implements KeypadChestBlock {
    private static final double AABB_OFFSET_BOTTOM = 0.0;
    private static final double AABB_OFFSET_SIDES = 0.06;
    private static final double AABB_OFFSET_TOP = 0.0625;
    private static final EnumMap<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    static {
        for (final var dir : Direction.values()) {
            SHAPES.put(dir, Shapes.create(computeAABB(dir)));
        }
    }

    public final Block unprotectedBlock;
    public final SkyChestType type;

    public KeypadSkyChestBlock(final SkyChestType type, final Properties props) {
        super(props);
        this.type = type;
        unprotectedBlock = switch (type) {
            case BLOCK -> AEBlocks.SMOOTH_SKY_STONE_CHEST.block();
            default -> AEBlocks.SKY_STONE_CHEST.block();
        };
        // @formatter:off
        registerDefaultState(defaultBlockState()
            .setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH)
            .setValue(WATERLOGGED, false));
        // @formatter:on
    }

    private static AABB computeAABB(final Direction up) {
        double offsetX = up.getStepX() == 0 ? AABB_OFFSET_SIDES : AABB_OFFSET_BOTTOM;
        double offsetY = up.getStepY() == 0 ? AABB_OFFSET_SIDES : AABB_OFFSET_BOTTOM;
        double offsetZ = up.getStepZ() == 0 ? AABB_OFFSET_SIDES : AABB_OFFSET_BOTTOM;
        double minX = Math.max(AABB_OFFSET_BOTTOM,
            offsetX + (up.getStepX() < 0 ? AABB_OFFSET_BOTTOM : (double) up.getStepX() * AABB_OFFSET_TOP));
        double minY = Math.max(AABB_OFFSET_BOTTOM,
            offsetY + (up.getStepY() < 0 ? AABB_OFFSET_TOP : (double) up.getStepY() * AABB_OFFSET_BOTTOM));
        double minZ = Math.max(AABB_OFFSET_BOTTOM,
            offsetZ + (up.getStepZ() < 0 ? AABB_OFFSET_BOTTOM : (double) up.getStepZ() * AABB_OFFSET_TOP));
        double maxX = Math.min(1.0,
            1.0 - offsetX - (up.getStepX() < 0 ? AABB_OFFSET_TOP : (double) up.getStepX() * AABB_OFFSET_BOTTOM));
        double maxY = Math.min(1.0,
            1.0 - offsetY - (up.getStepY() < 0 ? AABB_OFFSET_BOTTOM : (double) up.getStepY() * AABB_OFFSET_TOP));
        double maxZ = Math.min(1.0,
            1.0 - offsetZ - (up.getStepZ() < 0 ? AABB_OFFSET_TOP : (double) up.getStepZ() * AABB_OFFSET_BOTTOM));
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.horizontalFacing();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull RenderShape getRenderShape(final @NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean propagatesSkylightDown(final @NotNull BlockState state,
                                          final @NotNull BlockGetter reader,
                                          final @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public InteractionResult onActivated(final Level level,
                                         final BlockPos pos,
                                         final Player player,
                                         final InteractionHand hand,
                                         final @Nullable ItemStack heldItem,
                                         final BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult use(BlockState state,
                                 Level level,
                                 BlockPos pos,
                                 Player player,
                                 InteractionHand hand,
                                 BlockHitResult hit) {
        return useChest(state, level, pos, player, hand, hit);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(final @NotNull BlockState state,
                     final ServerLevel level,
                     final @NotNull BlockPos pos,
                     final @NotNull RandomSource random) {
        level.getBlockEntity(pos, AppengCompatibilityContent.keypadSkyChestBlockEntity.get()).ifPresent(
            SkyChestBlockEntity::recheckOpen);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(final @NotNull BlockState state,
                                        final @NotNull BlockGetter level,
                                        final @NotNull BlockPos pos,
                                        final @NotNull CollisionContext context) {
        final var disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);
        if (disguisedState.getBlock() != this) {
            return disguisedState.getShape(level, pos, context);
        }
        final var sk = getBlockEntity(level, pos);
        final var up = sk != null ? sk.getTop() : Direction.UP;
        return SHAPES.get(up);
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
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        final var fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(WATERLOGGED,
            fluidState.getType() == Fluids.WATER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull FluidState getFluidState(final @NotNull BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(final BlockState blockState,
                                           final @NotNull Direction facing,
                                           final @NotNull BlockState facingState,
                                           final @NotNull LevelAccessor level,
                                           final @NotNull BlockPos currentPos,
                                           final @NotNull BlockPos facingPos) {
        if (blockState.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(blockState, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public void setPlacedBy(final Level level,
                            final BlockPos pos,
                            final BlockState state,
                            final LivingEntity entity,
                            final ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (entity instanceof Player player) {
            MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
        }
    }

    @Override
    public void activate(final BlockState state, final Level level, final BlockPos pos, final Player player) {
        if (!level.isClientSide()) {
            final var blockEntity = getBlockEntity(level, pos);
            if (blockEntity != null) {
                MenuOpener.open(SkyChestMenu.TYPE, player, MenuLocators.forBlockEntity(blockEntity));
            }
        }
    }

    public SkyChestType getType() {
        return type;
    }
}
