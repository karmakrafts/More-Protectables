package io.karma.moreprotectables.client.hook;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 31/10/2024
 */
@OnlyIn(Dist.CLIENT)
public interface BlockColorsHooks {
    @Nullable
    BlockColor moreprotectable$getBlockColor(final Block block);

    @Nullable
    BlockColor moreprotectable$removeBlockColor(final Block block);
}
