package io.karma.moreprotectables.client.mixin;

import io.karma.moreprotectables.client.hook.ItemColorsHooks;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

/**
 * @author Alexander Hinze
 * @since 31/10/2024
 */
@Mixin(ItemColors.class)
public final class ItemColorsMixin implements ItemColorsHooks {
    @Shadow
    @Final
    private Map<Reference<Item>, ItemColor> itemColors;

    @Override
    public @Nullable ItemColor moreprotectables$removeItemColor(final Item item) {
        return itemColors.remove(ForgeRegistries.ITEMS.getDelegateOrThrow(item));
    }
}
