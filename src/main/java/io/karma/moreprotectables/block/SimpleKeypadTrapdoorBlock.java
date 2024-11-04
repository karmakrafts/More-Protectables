package io.karma.moreprotectables.block;

import io.karma.moreprotectables.blockentity.KeypadTrapdoorBlockEntity;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.misc.SaltData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;
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
    public void setPlacedBy(final @NotNull Level level,
                            final @NotNull BlockPos pos,
                            final @NotNull BlockState state,
                            @Nullable LivingEntity placer,
                            final @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos) instanceof IOwnable ownable && placer instanceof Player player) {
            ownable.setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
            if (stack.hasCustomHoverName() && ownable instanceof INameSetter nameSetter) {
                nameSetter.setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public void neighborChanged(final @NotNull BlockState state,
                                final @NotNull Level level,
                                final @NotNull BlockPos pos,
                                final @NotNull Block block,
                                final @NotNull BlockPos neighbor,
                                final boolean flag) {
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        return blockEntityType.get().create(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(final @NotNull BlockState pState,
                                          final @NotNull Level pLevel,
                                          final @NotNull BlockPos pPos,
                                          final @NotNull Player pPlayer,
                                          final @NotNull InteractionHand pHand,
                                          final @NotNull BlockHitResult pHit) {
        return useTrapdoor(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(final BlockState state,
                     final @NotNull ServerLevel level,
                     final @NotNull BlockPos pos,
                     final @NotNull RandomSource random) {
        if (state.getValue(OPEN)) {
            level.setBlockAndUpdate(pos, state.setValue(OPEN, false));
            playSound(null, level, pos, false);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(final BlockState state,
                         final @NotNull Level level,
                         final @NotNull BlockPos pos,
                         final BlockState newState,
                         final boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            final var var7 = level.getBlockEntity(pos);
            if (var7 instanceof IPasscodeProtected be) {
                SaltData.removeSalt(be.getSaltKey());
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean triggerEvent(final @NotNull BlockState state,
                                final @NotNull Level level,
                                final @NotNull BlockPos pos,
                                final int id,
                                final int param) {
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && be.triggerEvent(id, param);
    }
}
