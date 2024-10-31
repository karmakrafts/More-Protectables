package io.karma.moreprotectables.block;

import io.karma.moreprotectables.MoreProtectables;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 26/10/2024
 */
public final class ReinforcedShadowBlock extends OwnableBlock implements IReinforcedBlock {
    private final Block shadowedBlock;

    public ReinforcedShadowBlock(final Block shadowedBlock) {
        // @formatter:off
        super(Properties.of()
            .explosionResistance(shadowedBlock.explosionResistance)
            .sound(shadowedBlock.soundType)
            .friction(shadowedBlock.friction)
            .speedFactor(shadowedBlock.speedFactor)
            .jumpFactor(shadowedBlock.jumpFactor)
        );
        // @formatter:on
        if (shadowedBlock.isRandomlyTicking) {
            properties.randomTicks();
        }
        properties.canOcclude = shadowedBlock.properties.canOcclude;
        hasCollision = properties.hasCollision = shadowedBlock.properties.hasCollision;
        dynamicShape = properties.dynamicShape = shadowedBlock.properties.dynamicShape;
        this.shadowedBlock = shadowedBlock;
    }

    @Override
    public Block getVanillaBlock() {
        return shadowedBlock;
    }

    @Internal
    public void initAfterRegister() {
        // Update block state definition from the block we imitate
        final var builder = new StateDefinition.Builder<Block, BlockState>(this);
        shadowedBlock.createBlockStateDefinition(builder);
        stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
        // Update the default state and copy over all default properties from the shadowed block
        registerDefaultState(stateDefinition.any());
        registerDefaultState(withPropertiesOf(shadowedBlock.defaultBlockState()));
    }

    private BlockState getShadowedState(final @Nullable BlockState state) {
        if (state == null) {
            return shadowedBlock.defaultBlockState();
        }
        if (state.getBlock() != this) {
            return state; // Return the original if this isn't the matching shadowed block
        }
        return shadowedBlock.withPropertiesOf(state);
    }

    private void printShadowWarning(final String function) {
        MoreProtectables.LOGGER.warn("{} failed with generated reinforced block {}, please report this issue",
            function,
            ForgeRegistries.BLOCKS.getKey(this));
    }

    private <R> R tryDoShadowed(final BlockState state,
                                final String function,
                                final Supplier<R> defaultGetter,
                                final Function<BlockState, R> closure) {
        try {
            return closure.apply(getShadowedState(state));
        }
        catch (Throwable error) {
            printShadowWarning(function);
            return defaultGetter.get();
        }
    }

    private void tryDoShadowed(final BlockState state, final String function, final Consumer<BlockState> closure) {
        try {
            closure.accept(getShadowedState(state));
        }
        catch (Throwable error) {
            printShadowWarning(function);
        }
    }

    // Stubbed out cause we force-set stateDefinition through an AT
    @Override
    public void createBlockStateDefinition(final @NotNull Builder<Block, BlockState> builder) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(final @NotNull BlockState state,
                                          final @NotNull Level level,
                                          final @NotNull BlockPos pos,
                                          final @NotNull Player player,
                                          final @NotNull InteractionHand hand,
                                          final @NotNull BlockHitResult hit) {
        return tryDoShadowed(state,
            "Block#use",
            () -> super.use(state, level, pos, player, hand, hit),
            shadowedState -> shadowedBlock.use(shadowedState, level, pos, player, hand, hit));
    }

    @Override
    public void setPlacedBy(final @NotNull Level level,
                            final @NotNull BlockPos pos,
                            final @NotNull BlockState state,
                            final @Nullable LivingEntity placer,
                            final @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack); // Make sure owner is updated first
        tryDoShadowed(state,
            "Block#setPlacedBy",
            shadowedState -> shadowedBlock.setPlacedBy(level, pos, shadowedState, placer, stack));
    }

    @Override
    public BlockState getStateForPlacement(final @NotNull BlockPlaceContext context) {
        try {
            final var state = shadowedBlock.getStateForPlacement(context);
            if (state == null) {
                return defaultBlockState();
            }
            return withPropertiesOf(state);
        }
        catch (Throwable error) {
            MoreProtectables.LOGGER.warn(
                "Could not create block state for generated reinforced block {}, please report this issue",
                ForgeRegistries.BLOCKS.getKey(this));
            return defaultBlockState();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(final @NotNull BlockState state,
                        final @NotNull Level level,
                        final @NotNull BlockPos pos,
                        final @NotNull BlockState oldState,
                        final boolean movedByPiston) {
        tryDoShadowed(state,
            "Block#onPlace",
            shadowedState -> shadowedBlock.onPlace(shadowedState,
                level,
                pos,
                getShadowedState(oldState),
                movedByPiston));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(final @NotNull BlockState state,
                         final @NotNull Level level,
                         final @NotNull BlockPos pos,
                         final @NotNull BlockState newState,
                         final boolean movedByPiston) {
        tryDoShadowed(state,
            "Block#onRemove",
            shadowedState -> shadowedBlock.onRemove(shadowedState,
                level,
                pos,
                getShadowedState(newState),
                movedByPiston));
    }

    @Override
    public float getMaxHorizontalOffset() {
        return shadowedBlock.getMaxHorizontalOffset();
    }

    @Override
    public float getMaxVerticalOffset() {
        return shadowedBlock.getMaxVerticalOffset();
    }

    @Override
    public @NotNull FeatureFlagSet requiredFeatures() {
        return shadowedBlock.requiredFeatures;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPathfindable(final @NotNull BlockState state,
                                  final @NotNull BlockGetter level,
                                  final @NotNull BlockPos pos,
                                  final @NotNull PathComputationType type) {
        return tryDoShadowed(state,
            "Block#isPathfindable",
            () -> super.isPathfindable(state, level, pos, type),
            shadowedState -> shadowedBlock.isPathfindable(shadowedState, level, pos, type));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canBeReplaced(final @NotNull BlockState state, final @NotNull BlockPlaceContext context) {
        return shadowedBlock.canBeReplaced(getShadowedState(state), context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canBeReplaced(final @NotNull BlockState state, final @NotNull Fluid fluid) {
        return shadowedBlock.canBeReplaced(getShadowedState(state), fluid);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getOcclusionShape(final @NotNull BlockState state,
                                                 final @NotNull BlockGetter level,
                                                 final @NotNull BlockPos pos) {
        return tryDoShadowed(state,
            "Block#getOcclusionShape",
            () -> super.getOcclusionShape(state, level, pos),
            shadowedState -> shadowedBlock.getOcclusionShape(shadowedState, level, pos));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getBlockSupportShape(final @NotNull BlockState state,
                                                    final @NotNull BlockGetter level,
                                                    final @NotNull BlockPos pos) {
        return tryDoShadowed(state,
            "Block#getBlockSupportShape",
            () -> super.getBlockSupportShape(state, level, pos),
            shadowedState -> shadowedBlock.getBlockSupportShape(shadowedState, level, pos));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getInteractionShape(final @NotNull BlockState state,
                                                   final @NotNull BlockGetter level,
                                                   final @NotNull BlockPos pos) {
        return tryDoShadowed(state,
            "Block#getInteractionShape",
            () -> super.getInteractionShape(state, level, pos),
            shadowedState -> shadowedBlock.getInteractionShape(shadowedState, level, pos));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getCollisionShape(final @NotNull BlockState state,
                                                 final @NotNull BlockGetter level,
                                                 final @NotNull BlockPos pos,
                                                 final @NotNull CollisionContext context) {
        return tryDoShadowed(state,
            "Block#getCollisionShape",
            () -> super.getCollisionShape(state, level, pos, context),
            shadowedState -> shadowedBlock.getCollisionShape(shadowedState, level, pos, context));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getVisualShape(final @NotNull BlockState state,
                                              final @NotNull BlockGetter level,
                                              final @NotNull BlockPos pos,
                                              final @NotNull CollisionContext context) {
        return tryDoShadowed(state,
            "Block#getVisualShape",
            () -> super.getVisualShape(state, level, pos, context),
            shadowedState -> shadowedBlock.getVisualShape(shadowedState, level, pos, context));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(final @NotNull BlockState state,
                                           final @NotNull Direction direction,
                                           final @NotNull BlockState neighborState,
                                           final @NotNull LevelAccessor level,
                                           final @NotNull BlockPos pos,
                                           final @NotNull BlockPos neighborPos) {
        // @formatter:off
        return tryDoShadowed(state, "Block#updateShape",
            () -> super.updateShape(state, direction, neighborState, level, pos, neighborPos),
            shadowedState -> withPropertiesOf(shadowedBlock.updateShape(shadowedState,
                direction,
                getShadowedState(neighborState),
                level,
                pos,
                neighborPos)));
        // @formatter:on
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(final @NotNull BlockState state,
                                        final @NotNull BlockGetter level,
                                        final @NotNull BlockPos pos,
                                        final @NotNull CollisionContext context) {
        return tryDoShadowed(state,
            "Block#getShape",
            () -> super.getShape(state, level, pos, context),
            shadowedState -> shadowedBlock.getShape(shadowedState, level, pos, context));
    }

    @Override
    public void onNeighborChange(final BlockState state,
                                 final LevelReader level,
                                 final BlockPos pos,
                                 final BlockPos neighbor) {
        tryDoShadowed(state,
            "Block#onNeighborChange",
            shadowedState -> shadowedBlock.onNeighborChange(shadowedState, level, pos, neighbor));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(final @NotNull BlockState state,
                                final @NotNull Level level,
                                final @NotNull BlockPos pos,
                                final @NotNull Block neighborBlock,
                                final @NotNull BlockPos neighborPos,
                                final boolean movedByPiston) {
        tryDoShadowed(state,
            "Block#neighborChanged",
            shadowedState -> shadowedBlock.neighborChanged(shadowedState,
                level,
                pos,
                getShadowedState(neighborBlock.defaultBlockState()).getBlock(),
                neighborPos,
                movedByPiston));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull FluidState getFluidState(final @NotNull BlockState state) {
        return shadowedBlock.getFluidState(getShadowedState(state));
    }

    @Override
    public int getFlammability(final BlockState state,
                               final BlockGetter level,
                               final BlockPos pos,
                               final Direction direction) {
        return tryDoShadowed(state,
            "Block#getFlammability",
            () -> super.getFlammability(state, level, pos, direction),
            shadowedState -> shadowedBlock.getFlammability(shadowedState, level, pos, direction));
    }

    @Override
    public int getFireSpreadSpeed(final BlockState state,
                                  final BlockGetter level,
                                  final BlockPos pos,
                                  final Direction direction) {
        return tryDoShadowed(state,
            "Block#getFireSpreadSpeed",
            () -> super.getFireSpreadSpeed(state, level, pos, direction),
            shadowedState -> shadowedBlock.getFireSpreadSpeed(shadowedState, level, pos, direction));
    }

    @Override
    public void animateTick(final @NotNull BlockState state,
                            final @NotNull Level level,
                            final @NotNull BlockPos pos,
                            final @NotNull RandomSource random) {
        tryDoShadowed(state,
            "Block#animateTick",
            shadowedState -> shadowedBlock.animateTick(shadowedState, level, pos, random));
    }

    @Override
    public int getLightEmission(final BlockState state, final BlockGetter level, final BlockPos pos) {
        return tryDoShadowed(state,
            "Block#getLightEmission",
            () -> super.getLightEmission(state, level, pos),
            shadowedState -> shadowedBlock.getLightEmission(shadowedState, level, pos));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void updateIndirectNeighbourShapes(final @NotNull BlockState state,
                                              final @NotNull LevelAccessor level,
                                              final @NotNull BlockPos pos,
                                              final int flags,
                                              final int recursionLeft) {
        tryDoShadowed(state,
            "Block#updateIndirectNeighborShapes",
            shadowedState -> shadowedBlock.updateIndirectNeighbourShapes(shadowedState,
                level,
                pos,
                flags,
                recursionLeft));
    }

    @Override
    public MapColor getMapColor(final BlockState state,
                                final BlockGetter level,
                                final BlockPos pos,
                                final MapColor defaultColor) {
        return tryDoShadowed(state,
            "Block#getMapColor",
            () -> super.getMapColor(state, level, pos, defaultColor),
            shadowedState -> shadowedBlock.getMapColor(shadowedState, level, pos, defaultColor));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState mirror(final @NotNull BlockState state, final @NotNull Mirror mirror) {
        return withPropertiesOf(shadowedBlock.mirror(getShadowedState(state), mirror));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState rotate(final @NotNull BlockState state, final @NotNull Rotation rotation) {
        return withPropertiesOf(shadowedBlock.rotate(getShadowedState(state), rotation));
    }

    @Override
    public void onBlockStateChange(final LevelReader level,
                                   final BlockPos pos,
                                   final BlockState oldState,
                                   final BlockState newState) {
        tryDoShadowed(oldState,
            "Block#onBlockStateChange",
            shadowedState -> shadowedBlock.onBlockStateChange(level, pos, shadowedState, getShadowedState(newState)));
    }

    @Override
    public boolean propagatesSkylightDown(final @NotNull BlockState state,
                                          final @NotNull BlockGetter level,
                                          final @NotNull BlockPos pos) {
        return tryDoShadowed(state,
            "Block#propagatesSkylightDown",
            () -> super.propagatesSkylightDown(state, level, pos),
            shadowedState -> shadowedBlock.propagatesSkylightDown(shadowedState, level, pos));
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightBlock(final @NotNull BlockState state,
                             final @NotNull BlockGetter level,
                             final @NotNull BlockPos pos) {
        return tryDoShadowed(state,
            "Block#getLightBlock",
            () -> super.getLightBlock(state, level, pos),
            shadowedState -> shadowedBlock.getLightBlock(shadowedState, level, pos));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean skipRendering(final @NotNull BlockState state,
                                 final @NotNull BlockState adjacentState,
                                 final @NotNull Direction direction) {
        return shadowedBlock.skipRendering(getShadowedState(state), getShadowedState(adjacentState), direction);
    }

    @Override
    public void appendHoverText(final @NotNull ItemStack stack,
                                final @Nullable BlockGetter level,
                                final @NotNull List<Component> tooltip,
                                final @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        final var namespace = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(shadowedBlock)).getNamespace();
        final var modName = ModList.get().getModContainerById(namespace).orElseThrow().getModInfo().getDisplayName();
        tooltip.add(Component.translatable(String.format("tooltip.%s.reinforced_block", MoreProtectables.MODID),
            modName));
    }
}
