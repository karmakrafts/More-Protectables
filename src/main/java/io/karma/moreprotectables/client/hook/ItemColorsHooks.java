package io.karma.moreprotectables.client.hook;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexander Hinze
 * @since 31/10/2024
 */
@OnlyIn(Dist.CLIENT)
public interface ItemColorsHooks {
    @Nullable
    ItemColor moreprotectables$getItemColor(final Item item);
}
