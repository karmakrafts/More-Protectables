package io.karma.moreprotectables.compat.twilightforest;

import io.karma.moreprotectables.block.SimpleKeypadTrapdoorBlock;
import io.karma.moreprotectables.blockentity.KeypadTrapdoorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 25/10/2024
 */
public final class KeypadTFTrapdoorBlock extends SimpleKeypadTrapdoorBlock {
    public KeypadTFTrapdoorBlock(final Properties properties,
                                 final BlockSetType blockSetType,
                                 final Supplier<BlockEntityType<? extends KeypadTrapdoorBlockEntity>> blockEntityType) {
        super(properties, blockSetType, blockEntityType);
    }
}
