package io.karma.moreprotectables.util;

import net.minecraft.core.NonNullList;

/**
 * @author Alexander Hinze
 * @since 21/10/2024
 */
public final class ListUtils {
    // @formatter:off
    private ListUtils() {}
    // @formatter:on

    public static <T> NonNullList<T> copy(final NonNullList<T> list) {
        final var newList = NonNullList.<T>createWithCapacity(list.size());
        newList.addAll(list);
        return newList;
    }
}
