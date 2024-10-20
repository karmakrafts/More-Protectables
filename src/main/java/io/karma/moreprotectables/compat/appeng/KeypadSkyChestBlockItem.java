package io.karma.moreprotectables.compat.appeng;

import appeng.block.AEBaseBlockItem;
import io.karma.moreprotectables.client.render.BlockEntityItemRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Alexander Hinze
 * @since 15/10/2024
 */
public final class KeypadSkyChestBlockItem extends AEBaseBlockItem {
    public KeypadSkyChestBlockItem(final Block id, final Properties props) {
        super(id, props);
    }

    @Override
    public void initializeClient(final @NotNull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(BlockEntityItemRenderer.extension());
    }
}
