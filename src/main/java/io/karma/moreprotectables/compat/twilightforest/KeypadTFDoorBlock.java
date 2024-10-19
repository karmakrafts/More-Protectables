package io.karma.moreprotectables.compat.twilightforest;

import io.karma.moreprotectables.block.KeypadWoodDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public final class KeypadTFDoorBlock extends KeypadWoodDoorBlock {
    public KeypadTFDoorBlock(final Properties properties,
                             final BlockSetType type,
                             final Supplier<BlockEntityType<?>> blockEntityType) {
        super(properties, type, blockEntityType);
    }
}
