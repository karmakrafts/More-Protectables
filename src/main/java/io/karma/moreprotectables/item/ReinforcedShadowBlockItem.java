package io.karma.moreprotectables.item;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.block.ReinforcedShadowBlock;
import io.karma.moreprotectables.client.render.ReinforcedShadowBlockItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

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
        return Component.translatable(String.format("block.%s.reinforced_block", MoreProtectables.MODID),
            ((ReinforcedShadowBlock) getBlock()).getVanillaBlock().getName());
    }

    @Override
    public void initializeClient(final @NotNull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(ReinforcedShadowBlockItemRenderer.extension());
    }
}
