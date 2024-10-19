package io.karma.moreprotectables.init;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.util.KeypadDoorConvertible;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraftforge.fml.InterModComms;

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
            InterModComms.sendTo(SecurityCraft.MODID,
                SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
                () -> new KeypadDoorConvertible(ModBlocks.WOOD_DOORS.get(woodType),
                    ModBlocks.KEYPAD_WOOD_DOOR.get(woodType).get()));
        }
    }
}
