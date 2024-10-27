package io.karma.moreprotectables.item;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.block.ReinforcedShadowBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 26/10/2024
 */
public final class ReinforcedShadowBlockItem extends BlockItem {
    public ReinforcedShadowBlockItem(final ReinforcedShadowBlock block, final Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(final @NotNull ItemStack stack) {
        final var item = ((ReinforcedShadowBlock) getBlock()).getShadowedBlock().asItem();
        final var shadowedStack = new ItemStack(item);
        return Component.translatable(String.format("block.%s.reinforced_block", MoreProtectables.MODID),
            item.getName(shadowedStack));
    }
}
