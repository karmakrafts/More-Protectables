package io.karma.moreprotectables.compat.twilightforest;

import io.karma.moreprotectables.client.render.BlockEntityItemRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
public final class KeypadTFChestBlockItem extends BlockItem {
    public KeypadTFChestBlockItem(final Block block, final Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(final @NotNull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(BlockEntityItemRenderer.extension());
    }
}
