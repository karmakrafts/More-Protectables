package io.karma.moreprotectables.util;

import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public final class WoodTypeUtils {
    // @formatter:off
    private WoodTypeUtils() {}
    // @formatter:on

    public static String getSimpleName(final WoodType woodType) {
        var name = woodType.name();
        if (name.contains(":")) {
            name = name.substring(name.indexOf(":") + 1);
        }
        return name;
    }
}
