package io.karma.moreprotectables.client.hooks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ItemRendererHooks {
    BlockEntity moreprotectables$getBlockEntity(final ItemStack stack);
}
