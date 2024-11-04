package io.karma.moreprotectables.item;

import net.minecraft.world.item.Item;

/**
 * @author Alexander Hinze
 * @since 01/11/2024
 */
public final class ABRItem extends Item {
    private final Type type;

    public ABRItem(final Type type, final Properties properties) {
        super(properties);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        // @formatter:off
        LEVEL_1(2),
        LEVEL_2(4),
        LEVEL_3(8);
        // @formatter:on

        private final int radius;

        Type(final int radius) {
            this.radius = radius;
        }

        public int getRadius() {
            return radius;
        }
    }
}
