package io.karma.moreprotectables.init;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.block.SimpleKeypadDoorBlock;
import io.karma.moreprotectables.block.SimpleKeypadTrapdoorBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public final class ModBlocks {
    public static final HashMap<WoodType, RegistryObject<SimpleKeypadDoorBlock>> KEYPAD_WOOD_DOOR = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<SimpleKeypadTrapdoorBlock>> KEYPAD_WOOD_TRAPDOOR = new HashMap<>();
    public static final HashMap<WoodType, Block> WOOD_DOORS = new HashMap<>();
    public static final HashMap<WoodType, Block> WOOD_TRAPDOORS = new HashMap<>();
    public static RegistryObject<SimpleKeypadDoorBlock> KEYPAD_IRON_DOOR;

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

        WOOD_TRAPDOORS.put(WoodType.OAK, Blocks.OAK_TRAPDOOR);
        WOOD_TRAPDOORS.put(WoodType.SPRUCE, Blocks.SPRUCE_TRAPDOOR);
        WOOD_TRAPDOORS.put(WoodType.BIRCH, Blocks.BIRCH_TRAPDOOR);
        WOOD_TRAPDOORS.put(WoodType.ACACIA, Blocks.ACACIA_TRAPDOOR);
        WOOD_TRAPDOORS.put(WoodType.CHERRY, Blocks.CHERRY_TRAPDOOR);
        WOOD_TRAPDOORS.put(WoodType.JUNGLE, Blocks.JUNGLE_TRAPDOOR);
        WOOD_TRAPDOORS.put(WoodType.DARK_OAK, Blocks.DARK_OAK_TRAPDOOR);
        WOOD_TRAPDOORS.put(WoodType.CRIMSON, Blocks.CRIMSON_TRAPDOOR);
        WOOD_TRAPDOORS.put(WoodType.WARPED, Blocks.WARPED_TRAPDOOR);
        WOOD_TRAPDOORS.put(WoodType.MANGROVE, Blocks.MANGROVE_TRAPDOOR);
        WOOD_TRAPDOORS.put(WoodType.BAMBOO, Blocks.BAMBOO_TRAPDOOR);
    }

    // @formatter:off
    private ModBlocks() {}
    // @formatter:on

    public static void register() {
        for (final var woodType : MoreProtectables.WOOD_TYPES) {
            KEYPAD_WOOD_DOOR.put(woodType,
                MoreProtectables.block(String.format("keypad_%s_door", woodType.name()),
                    () -> new SimpleKeypadDoorBlock(BlockBehaviour.Properties.copy(WOOD_DOORS.get(woodType)),
                        woodType.setType(),
                        ModBlockEntities.KEYPAD_WOOD_DOOR.get(woodType)::get),
                    BlockItem::new));

            KEYPAD_WOOD_TRAPDOOR.put(woodType,
                MoreProtectables.block(String.format("keypad_%s_trapdoor", woodType.name()),
                    () -> new SimpleKeypadTrapdoorBlock(BlockBehaviour.Properties.copy(WOOD_TRAPDOORS.get(woodType)),
                        woodType.setType(),
                        ModBlockEntities.KEYPAD_WOOD_TRAPDOOR.get(woodType)::get),
                    BlockItem::new));
        }

        KEYPAD_IRON_DOOR = MoreProtectables.block("keypad_iron_door",
            () -> new SimpleKeypadDoorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_DOOR),
                BlockSetType.IRON,
                ModBlockEntities.KEYPAD_IRON_DOOR::get),
            BlockItem::new);
    }
}
