package io.karma.moreprotectables.blockentity;

import io.karma.moreprotectables.block.KeypadBlock;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.entity.sentry.ISentryBulletContainer;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public interface KeypadChestBlockEntity extends KeypadBlockEntity, ILockable, ISentryBulletContainer, LidBlockEntity {
    @Override
    default LazyOptional<IItemHandler> getHandlerForSentry(Sentry entity) {
        return entity.getOwner().owns(this) ? getThis().getCapability(ForgeCapabilities.ITEM_HANDLER,
            Direction.UP) : LazyOptional.empty();
    }

    @Override
    default boolean isOpen() {
        return getOpenNess(0F) > 0F;
    }

    @Override
    default ModuleType[] acceptedModules() {
        return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.REDSTONE, ModuleType.SMART, ModuleType.HARMING, ModuleType.DISGUISE};
    }

    default boolean isBlocked() {
        final var blockEntity = getThis();
        if (!blockEntity.hasLevel()) {
            return true;
        }
        final var level = Objects.requireNonNull(blockEntity.getLevel());
        for (final var dir : Direction.Plane.HORIZONTAL) {
            final var pos = blockEntity.getBlockPos().relative(dir);
            if (level.getBlockState(pos).getBlock() == blockEntity.getBlockState().getBlock() && net.geforcemods.securitycraft.blocks.KeypadChestBlock.isBlocked(
                level,
                pos)) {
                return true;
            }
        }
        return net.geforcemods.securitycraft.blocks.KeypadChestBlock.isBlocked(Objects.requireNonNull(level),
            blockEntity.getBlockPos());
    }

    @Override
    default void activate(final Player player) {
        final var blockEntity = getThis();
        if (!blockEntity.hasLevel()) {
            return;
        }
        final var level = Objects.requireNonNull(blockEntity.getLevel());
        if (!level.isClientSide && blockEntity.getBlockState().getBlock() instanceof KeypadBlock block && !isBlocked()) {
            block.activate(blockEntity.getBlockState(), level, blockEntity.getBlockPos(), player);
        }
    }

    @Override
    default boolean isPrimaryBlock() {
        final var other = findOtherBlock();
        if (other == null) {
            return true;
        }
        final var pos = getThisPos();
        final var state = getThisState();
        final var facing = state.getValue(HorizontalDirectionalBlock.FACING);
        final var otherPos = other.getBlockPos();
        // @formatter:off
        if ((facing.getAxis() == Axis.X && facing.getAxisDirection() == AxisDirection.NEGATIVE)
            || (facing.getAxis() == Axis.Z && facing.getAxisDirection() == AxisDirection.POSITIVE)) {
            return otherPos.equals(pos.offset(X_AXIS)) || otherPos.equals(pos.offset(Z_AXIS));
        }
        // @formatter:on
        return otherPos.equals(pos.subtract(X_AXIS)) || otherPos.equals(pos.subtract(Z_AXIS));
    }

    @Override
    default @Nullable BlockEntity findOtherBlock() {
        final var state = getThisState();
        if (!state.hasProperty(ChestBlock.TYPE)) {
            return null;
        }
        final var type = state.getValue(ChestBlock.TYPE);
        if (type != ChestType.SINGLE) {
            final var offsetPos = getThisPos().relative(ChestBlock.getConnectedDirection(state));
            final var level = Objects.requireNonNull(getThis().getLevel());
            final var offsetState = level.getBlockState(offsetPos);
            if (state.getBlock() == offsetState.getBlock()) {
                final var offsetType = offsetState.getValue(ChestBlock.TYPE);
                if (offsetType != ChestType.SINGLE && type != offsetType && state.getValue(ChestBlock.FACING) == offsetState.getValue(
                    ChestBlock.FACING)) {
                    return level.getBlockEntity(offsetPos);
                }
            }
        }
        return null;
    }
}
