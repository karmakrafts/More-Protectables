package io.karma.moreprotectables.compat.tropicraft;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.tropicraft.core.client.TropicraftItemRenderers;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public final class KeypadBambooChestBlockItem extends BlockItem {
    public KeypadBambooChestBlockItem(final Block block, final Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(final @NotNull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(TropicraftItemRenderers.bambooChest());
    }
}
