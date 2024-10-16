package io.karma.moreprotectables.compat.ironchest;

import com.progwml6.ironchest.common.block.IronChestsTypes;
import com.progwml6.ironchest.common.block.regular.AbstractIronChestBlock;
import io.karma.moreprotectables.util.KeypadChestBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 15/10/2024
 */
public final class KeypadIronChestBlock extends AbstractIronChestBlock implements KeypadChestBlock {
    public KeypadIronChestBlock(final IronChestsTypes type, final Properties properties) {
        super(properties, () -> IronChestCompatibilityContent.getKeypadChestBlockEntityType(type), type);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final @NotNull Level level,
                                                                  final @NotNull BlockState state,
                                                                  final @NotNull BlockEntityType<T> blockEntityType) {
        // Attach server ticker for updating crystal chests
        if (!level.isClientSide && blockEntityType == IronChestCompatibilityContent.keypadCrystalChestBlockEntity.get()) {
            return KeypadCrystalChestBlockEntity::tick;
        }
        return super.getTicker(level, state, blockEntityType);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        return blockEntityType.get().create(pos, state);
    }
}
