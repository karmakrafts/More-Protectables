package io.karma.moreprotectables.compat.tropicraft;

import io.karma.moreprotectables.block.SimpleKeypadTrapdoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/**
 * @author Alexander Hinze
 * @since 25/10/2024
 */
public final class KeypadBambooTrapdoorBlock extends SimpleKeypadTrapdoorBlock {
    public KeypadBambooTrapdoorBlock(final Properties properties, final BlockSetType blockSetType) {
        super(properties, blockSetType, TropicraftCompatibilityContent.keypadBambooTrapdoorEntity::get);
    }
}
