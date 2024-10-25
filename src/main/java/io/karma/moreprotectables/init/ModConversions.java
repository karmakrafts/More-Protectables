package io.karma.moreprotectables.init;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.util.PasscodeConversions;
import net.minecraft.world.level.block.Blocks;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public final class ModConversions {
    // @formatter:off
    private ModConversions() {}
    // @formatter:on

    public static void register() {
        for (final var woodType : MoreProtectables.WOOD_TYPES) {
            PasscodeConversions.registerDoorConversion(() -> ModBlocks.WOOD_DOORS.get(woodType),
                ModBlocks.KEYPAD_WOOD_DOOR.get(woodType));
            PasscodeConversions.registerTrapdoorConversion(() -> ModBlocks.WOOD_TRAPDOORS.get(woodType),
                ModBlocks.KEYPAD_WOOD_TRAPDOOR.get(woodType));
        }
        PasscodeConversions.registerDoorConversion(() -> Blocks.IRON_DOOR, ModBlocks.KEYPAD_IRON_DOOR);
    }
}
