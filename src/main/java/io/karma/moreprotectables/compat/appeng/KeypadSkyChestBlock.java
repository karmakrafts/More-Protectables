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
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
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
        if (!level.isClientSide()) {
            SkyChestBlockEntity blockEntity = getBlockEntity(level, pos);
            if (blockEntity != null) {
                MenuOpener.open(SkyChestMenu.TYPE, player, MenuLocators.forBlockEntity(blockEntity));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
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
        SkyChestBlockEntity sk = getBlockEntity(level, pos);
        Direction up = sk != null ? sk.getTop() : Direction.UP;
        return SHAPES.get(up);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
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
    public @NotNull BlockState updateShape(BlockState blockState,
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

    private final class Convertible implements IPasscodeConvertible {
        @Override
        public boolean isUnprotectedBlock(final BlockState state) {
            return state.is(unprotectedBlock);
        }

        @Override
        public boolean isProtectedBlock(final BlockState state) {
            return state.is(KeypadSkyChestBlock.this);
        }

        @Override
        public boolean protect(final Player player, final Level level, final BlockPos pos) {
            return false;
        }

        @Override
        public boolean unprotect(final Player player, final Level level, final BlockPos pos) {
            return false;
        }
    }
}
