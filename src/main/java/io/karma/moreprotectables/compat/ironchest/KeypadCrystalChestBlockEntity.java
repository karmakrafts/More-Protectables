package io.karma.moreprotectables.compat.ironchest;

import com.progwml6.ironchest.common.block.IronChestsTypes;
import com.progwml6.ironchest.common.block.entity.ICrystalChest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
public final class KeypadCrystalChestBlockEntity extends KeypadIronChestBlockEntity implements ICrystalChest {
    private NonNullList<ItemStack> topStacks = NonNullList.withSize(8, ItemStack.EMPTY);
    private boolean inventoryTouched;
    private boolean hadStuff;

    public KeypadCrystalChestBlockEntity(final IronChestsTypes type,
                                         final Supplier<Block> block,
                                         final BlockPos pos,
                                         final BlockState state) {
        super(type, block, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!(blockEntity instanceof KeypadCrystalChestBlockEntity chestBlockEntity)) {
            return;
        }
        if (chestBlockEntity.inventoryTouched) {
            chestBlockEntity.inventoryTouched = false;
            chestBlockEntity.sortTopStacks();
        }
    }

    @Override
    public void setItems(final @NotNull NonNullList<ItemStack> items) {
        super.setItems(items);
        inventoryTouched = true;
    }

    @Override
    public @NotNull ItemStack getItem(final int index) {
        inventoryTouched = true;
        return super.getItem(index);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getTopItems() {
        return topStacks;
    }

    @Nullable
    @Override
    public Level getChestLevel() {
        return level;
    }

    @Override
    public @NotNull BlockPos getChestWorldPosition() {
        return worldPosition;
    }

    @Override
    public void receiveMessageFromServer(final @NotNull NonNullList<ItemStack> topStacks) {
        this.topStacks = topStacks;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getCurrentItems() {
        return getItems();
    }

    @Override
    public boolean getHadStuff() {
        return hadStuff;
    }

    @Override
    public void setHadStuff(final boolean hadStuff) {
        this.hadStuff = hadStuff;
    }
}
