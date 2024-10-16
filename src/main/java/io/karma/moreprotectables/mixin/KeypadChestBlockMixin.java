package io.karma.moreprotectables.mixin;

import io.karma.moreprotectables.util.KeypadChestBlock;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
@Mixin(value = net.geforcemods.securitycraft.blocks.KeypadChestBlock.class, remap = false)
public class KeypadChestBlockMixin implements KeypadChestBlock {
}
