package io.karma.moreprotectables.util;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.InterModComms;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 25/10/2024
 */
public final class PasscodeConversions {
    // @formatter:off
    private PasscodeConversions() {}
    // @formatter:on

    public static void registerChestConversion(final Supplier<? extends Block> unprot,
                                               final Supplier<? extends Block> prot) {
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new KeypadChestConvertible(unprot.get(), prot.get()));
    }

    public static void registerDoorConversion(final Supplier<? extends Block> unprot,
                                              final Supplier<? extends Block> prot) {
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new KeypadDoorConvertible(unprot.get(), prot.get()));
    }

    public static void registerTrapdoorConversion(final Supplier<? extends Block> unprot,
                                                  final Supplier<? extends Block> prot) {
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new KeypadTrapdoorConvertible(unprot.get(), prot.get()));
    }
}
