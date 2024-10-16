package io.karma.moreprotectables.compat.ironchest;

import com.progwml6.ironchest.common.block.IronChestsTypes;
import com.progwml6.ironchest.common.block.regular.entity.AbstractIronChestBlockEntity;
import com.progwml6.ironchest.common.inventory.IronChestMenu;
import io.karma.moreprotectables.util.KeypadChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 15/10/2024
 */
public class KeypadIronChestBlockEntity extends AbstractIronChestBlockEntity implements KeypadChestBlockEntity {
    public KeypadIronChestBlockEntity(final IronChestsTypes type,
                                      final Supplier<Block> block,
                                      final BlockPos pos,
                                      final BlockState state) {
        super(IronChestCompatibilityContent.getKeypadChestBlockEntityType(type), pos, state, type, block);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(final int containerId,
                                                        final @NotNull Inventory playerInventory) {
        return switch (getChestType()) {
            case COPPER -> IronChestMenu.createCopperContainer(containerId, playerInventory, this);
            case GOLD -> IronChestMenu.createGoldContainer(containerId, playerInventory, this);
            case DIAMOND -> IronChestMenu.createDiamondContainer(containerId, playerInventory, this);
            case CRYSTAL -> IronChestMenu.createCrystalContainer(containerId, playerInventory, this);
            case OBSIDIAN -> IronChestMenu.createObsidianContainer(containerId, playerInventory, this);
            default -> IronChestMenu.createIronContainer(containerId, playerInventory, this);
        };
    }

    @Nullable
    @Override
    public BlockEntity findOtherChest() {
        return null;
    }

    @Override
    public BlockPos getBEPos() {
        return worldPosition;
    }

    @Override
    public BlockState getBEBlockState() {
        return getBlockState();
    }

    @Override
    public boolean isOpen() {
        return getOpenNess(0F) > 0F;
    }
}
