package io.karma.moreprotectables.item;

import io.karma.moreprotectables.client.render.DelegateBlockItemRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 26/10/2024
 */
public class SimpleKeypadBlockItem extends BlockItem {
    public SimpleKeypadBlockItem(final Block block, final Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(final @NotNull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(DelegateBlockItemRenderer.extension());
    }
}
