package io.karma.moreprotectables.compat.ironchest;

import com.progwml6.ironchest.common.block.IronChestsTypes;
import com.progwml6.ironchest.common.block.regular.AbstractIronChestBlock;
import com.progwml6.ironchest.common.block.regular.entity.AbstractIronChestBlockEntity;
import com.progwml6.ironchest.common.item.ChestUpgradeItem;
import io.karma.moreprotectables.block.KeypadChestBlock;
import io.karma.moreprotectables.compat.ironchest.hooks.ChestUpgradeItemHooks;
import io.karma.moreprotectables.util.ListUtils;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 15/10/2024
 */
public final class KeypadIronChestBlock extends AbstractIronChestBlock implements KeypadChestBlock {
    public KeypadIronChestBlock(final IronChestsTypes type, final Properties properties) {
        super(properties, IronChestCompatibilityContent.KEYPAD_CHEST_BLOCK_ENTITIES.get(type)::get, type);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.SOUTH));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final @NotNull Level level,
                                                                  final @NotNull BlockState state,
                                                                  final @NotNull BlockEntityType<T> blockEntityType) {
        // Attach ticker for updating crystal chests
        if (blockEntityType == IronChestCompatibilityContent.KEYPAD_CHEST_BLOCK_ENTITIES.get(IronChestsTypes.CRYSTAL).get()) {
            return level.isClientSide ? createTickerHelper(blockEntityType,
                this.blockEntityType(),
                AbstractIronChestBlockEntity::lidAnimateTick) : createTickerHelper(blockEntityType,
                this.blockEntityType(),
                KeypadCrystalChestBlockEntity::tick);
        }
        return super.getTicker(level, state, blockEntityType);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        return blockEntityType.get().create(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(final @NotNull BlockState state,
                                          final @NotNull Level level,
                                          final @NotNull BlockPos pos,
                                          final @NotNull Player player,
                                          final @NotNull InteractionHand hand,
                                          final @NotNull BlockHitResult hit) {
        final var stack = player.getItemInHand(hand);
        final var item = stack.getItem();
        if (!(item instanceof ChestUpgradeItem upgradeItem)) {
            return useChest(state, level, pos, player, hand, hit);
        }
        final var type = ((ChestUpgradeItemHooks) upgradeItem).moreprotectables$getType();

        final var blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof KeypadIronChestBlockEntity chestBlockEntity)) {
            return InteractionResult.FAIL;
        }
        final var owner = chestBlockEntity.getOwner();
        if (!player.getUUID().toString().equals(owner.getUUID())) {
            return InteractionResult.FAIL;
        }
        if (!type.canUpgrade(chestBlockEntity.getChestType())) {
            return InteractionResult.PASS;
        }
        final var customName = chestBlockEntity.getCustomName();
        final var items = ListUtils.copy(chestBlockEntity.getItems());
        final var modules = ListUtils.copy(chestBlockEntity.getInventory());
        final var saltKey = chestBlockEntity.getSaltKey();
        final var passcode = chestBlockEntity.getPasscode();

        chestBlockEntity.clearContent(); // Make sure the original chest doesn't spill its contents..

        // @formatter:off
        level.setBlockAndUpdate(pos, IronChestCompatibilityContent.KEYPAD_CHEST_BLOCKS.get(type.target).get()
            .defaultBlockState()
            .setValue(ChestBlock.FACING, state.getValue(ChestBlock.FACING))
            .setValue(ChestBlock.WATERLOGGED, state.getValue(ChestBlock.WATERLOGGED)));
        // @formatter:on

        final var newBlockEntity = level.getBlockEntity(pos);
        if (!(newBlockEntity instanceof KeypadIronChestBlockEntity newChestBlockEntity)) {
            return InteractionResult.FAIL;
        }
        newChestBlockEntity.setOwner(owner.getUUID(), owner.getName());
        newChestBlockEntity.setSaltKey(saltKey);
        if (passcode != null) {
            newChestBlockEntity.setPasscode(passcode);
        }
        if (customName != null) {
            newChestBlockEntity.setCustomName(customName);
        }
        newChestBlockEntity.setInventory(modules);
        newChestBlockEntity.setItems(items);
        newChestBlockEntity.setChanged();

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.SUCCESS;
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
                            final LivingEntity entity,
                            final @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (entity instanceof Player player) {
            MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
        }
    }
}
