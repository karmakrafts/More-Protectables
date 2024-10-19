package io.karma.moreprotectables.mixin;

import io.karma.moreprotectables.block.KeypadChestBlock;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
@Mixin(net.geforcemods.securitycraft.blocks.KeypadChestBlock.class)
public abstract class KeypadChestBlockMixin implements KeypadChestBlock {
}
