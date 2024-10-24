package io.karma.moreprotectables.mixin;

import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
@Mixin(value = KeypadChestBlockEntity.class)
public abstract class KeypadChestBlockEntityMixin extends ChestBlockEntity
    implements io.karma.moreprotectables.blockentity.KeypadChestBlockEntity {
    protected KeypadChestBlockEntityMixin(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    @Shadow(remap = false)
    public abstract KeypadChestBlockEntity findOther();

    @SuppressWarnings("all")
    @Override
    public BlockEntity findOtherBlock() {
        return findOther();
    }

    @SuppressWarnings("all")
    @Override
    public BlockPos getThisPos() {
        return worldPosition;
    }

    @SuppressWarnings("all")
    @Override
    public BlockState getThisState() {
        return getBlockState();
    }
}
