package io.karma.moreprotectables.init;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.item.ABRItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Locale;

/**
 * @author Alexander Hinze
 * @since 01/11/2024
 */
public final class ModItems {
    public static final EnumMap<ABRItem.Type, RegistryObject<ABRItem>> ADVANCED_BLOCK_REINFORCER = new EnumMap<>(ABRItem.Type.class);

    // @formatter:off
    private ModItems() {}
    // @formatter:on

    public static void register() {
        for (final var type : ABRItem.Type.values()) {
            ADVANCED_BLOCK_REINFORCER.put(type,
                MoreProtectables.item(String.format("advanced_block_reinforcer_%s",
                    type.name().toLowerCase(Locale.ROOT)), () -> new ABRItem(type, new Item.Properties())));
        }
    }
}
