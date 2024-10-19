package io.karma.moreprotectables.init;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.block.KeypadWoodDoorBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public final class ModBlocks {
    public static final HashMap<WoodType, RegistryObject<KeypadWoodDoorBlock>> KEYPAD_WOOD_DOOR = new HashMap<>();
    public static final HashMap<WoodType, Block> WOOD_DOORS = new HashMap<>();

    static {
        WOOD_DOORS.put(WoodType.OAK, Blocks.OAK_DOOR);
        WOOD_DOORS.put(WoodType.SPRUCE, Blocks.SPRUCE_DOOR);
        WOOD_DOORS.put(WoodType.BIRCH, Blocks.BIRCH_DOOR);
        WOOD_DOORS.put(WoodType.ACACIA, Blocks.ACACIA_DOOR);
        WOOD_DOORS.put(WoodType.CHERRY, Blocks.CHERRY_DOOR);
        WOOD_DOORS.put(WoodType.JUNGLE, Blocks.JUNGLE_DOOR);
        WOOD_DOORS.put(WoodType.DARK_OAK, Blocks.DARK_OAK_DOOR);
        WOOD_DOORS.put(WoodType.CRIMSON, Blocks.CRIMSON_DOOR);
        WOOD_DOORS.put(WoodType.WARPED, Blocks.WARPED_DOOR);
        WOOD_DOORS.put(WoodType.MANGROVE, Blocks.MANGROVE_DOOR);
        WOOD_DOORS.put(WoodType.BAMBOO, Blocks.BAMBOO_DOOR);
    }

    // @formatter:off
    private ModBlocks() {}
    // @formatter:on

    public static void register() {
        for (final var woodType : MoreProtectables.WOOD_TYPES) {
            final var name = String.format("keypad_%s_door", woodType.name());
            final var props = BlockBehaviour.Properties.copy(WOOD_DOORS.get(woodType));
            KEYPAD_WOOD_DOOR.put(woodType,
                MoreProtectables.block(name,
                    () -> new KeypadWoodDoorBlock(props,
                        woodType.setType(),
                        ModBlockEntities.KEYPAD_WOOD_DOOR.get(woodType)::get),
                    BlockItem::new));
        }
    }
}
