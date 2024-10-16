package io.karma.moreprotectables.mixin;

import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
@Mixin(value = KeypadChestBlockEntity.class, remap = false)
public abstract class KeypadChestBlockEntityMixin extends ChestBlockEntity
    implements io.karma.moreprotectables.util.KeypadChestBlockEntity {
    @Unique
    private static final Vec3i moreprotectables$X_AXIS = new Vec3i(1, 0, 0);
    @Unique
    private static final Vec3i moreprotectables$Z_AXIS = new Vec3i(0, 0, 1);

    protected KeypadChestBlockEntityMixin(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    public abstract KeypadChestBlockEntity findOther();

    @SuppressWarnings("all")
    @Override
    public boolean isDoubleChest() {
        return findOther() != null;
    }

    @SuppressWarnings("all")
    @Override
    public boolean isPrimaryChest() {
        final var other = findOther();
        if (other == null) {
            return true;
        }
        final var pos = getBlockPos();
        final var otherPos = other.getBlockPos();
        return otherPos.equals(pos.subtract(moreprotectables$X_AXIS)) || otherPos.equals(pos.subtract(
            moreprotectables$Z_AXIS));
    }
}
