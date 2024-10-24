package io.karma.moreprotectables.compat.twilightforest;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.block.SimpleKeypadDoorBlock;
import io.karma.moreprotectables.blockentity.SimpleKeypadDoorBlockEntity;
import io.karma.moreprotectables.util.WoodTypeUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.RegistryObject;
import twilightforest.block.TFChestBlock;
import twilightforest.init.TFBlocks;
import twilightforest.util.TFWoodTypes;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
public final class TFCompatibilityContent {
    // @formatter:off
    public static final WoodType[] WOOD_TYPES = {
        TFWoodTypes.TWILIGHT_OAK_WOOD_TYPE,
        TFWoodTypes.CANOPY_WOOD_TYPE,
        TFWoodTypes.MANGROVE_WOOD_TYPE,
        TFWoodTypes.DARK_WOOD_TYPE,
        TFWoodTypes.TIME_WOOD_TYPE,
        TFWoodTypes.TRANSFORMATION_WOOD_TYPE,
        TFWoodTypes.MINING_WOOD_TYPE,
        TFWoodTypes.SORTING_WOOD_TYPE
    };
    // @formatter:on

    public static final HashMap<WoodType, RegistryObject<TFChestBlock>> WOOD_CHEST_BLOCKS = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<DoorBlock>> WOOD_DOOR_BLOCKS = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<KeypadTFChestBlock>> KEYPAD_WOOD_CHEST_BLOCKS = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<BlockEntityType<KeypadTFChestBlockEntity>>> KEYPAD_WOOD_CHEST_ENTITIES = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<SimpleKeypadDoorBlock>> KEYPAD_WOOD_DOOR_BLOCKS = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<BlockEntityType<SimpleKeypadDoorBlockEntity>>> KEYPAD_WOOD_DOOR_ENTITIES = new HashMap<>();

    static {
        WOOD_CHEST_BLOCKS.put(TFWoodTypes.TWILIGHT_OAK_WOOD_TYPE, TFBlocks.TWILIGHT_OAK_CHEST);
        WOOD_CHEST_BLOCKS.put(TFWoodTypes.CANOPY_WOOD_TYPE, TFBlocks.CANOPY_CHEST);
        WOOD_CHEST_BLOCKS.put(TFWoodTypes.MANGROVE_WOOD_TYPE, TFBlocks.MANGROVE_CHEST);
        WOOD_CHEST_BLOCKS.put(TFWoodTypes.DARK_WOOD_TYPE, TFBlocks.DARK_CHEST);
        WOOD_CHEST_BLOCKS.put(TFWoodTypes.TIME_WOOD_TYPE, TFBlocks.TIME_CHEST);
        WOOD_CHEST_BLOCKS.put(TFWoodTypes.TRANSFORMATION_WOOD_TYPE, TFBlocks.TRANSFORMATION_CHEST);
        WOOD_CHEST_BLOCKS.put(TFWoodTypes.MINING_WOOD_TYPE, TFBlocks.MINING_CHEST);
        WOOD_CHEST_BLOCKS.put(TFWoodTypes.SORTING_WOOD_TYPE, TFBlocks.SORTING_CHEST);

        WOOD_DOOR_BLOCKS.put(TFWoodTypes.TWILIGHT_OAK_WOOD_TYPE, TFBlocks.TWILIGHT_OAK_DOOR);
        WOOD_DOOR_BLOCKS.put(TFWoodTypes.CANOPY_WOOD_TYPE, TFBlocks.CANOPY_DOOR);
        WOOD_DOOR_BLOCKS.put(TFWoodTypes.MANGROVE_WOOD_TYPE, TFBlocks.MANGROVE_DOOR);
        WOOD_DOOR_BLOCKS.put(TFWoodTypes.DARK_WOOD_TYPE, TFBlocks.DARK_DOOR);
        WOOD_DOOR_BLOCKS.put(TFWoodTypes.TIME_WOOD_TYPE, TFBlocks.TIME_DOOR);
        WOOD_DOOR_BLOCKS.put(TFWoodTypes.TRANSFORMATION_WOOD_TYPE, TFBlocks.TRANSFORMATION_DOOR);
        WOOD_DOOR_BLOCKS.put(TFWoodTypes.MINING_WOOD_TYPE, TFBlocks.MINING_DOOR);
        WOOD_DOOR_BLOCKS.put(TFWoodTypes.SORTING_WOOD_TYPE, TFBlocks.SORTING_DOOR);
    }

    // @formatter:off
    private TFCompatibilityContent() {}
    // @formatter:on

    public static void register() {
        for (final var woodType : WOOD_TYPES) {
            final var woodName = WoodTypeUtils.getSimpleName(woodType);
            final var chestName = String.format("keypad_tf_%s_chest", woodName);
            final var doorName = String.format("keypad_tf_%s_door", woodName);
            final var chestBlock = WOOD_CHEST_BLOCKS.get(woodType);
            final var doorBlock = WOOD_DOOR_BLOCKS.get(woodType);

            KEYPAD_WOOD_CHEST_BLOCKS.put(woodType,
                MoreProtectables.block(chestName,
                    () -> new KeypadTFChestBlock(BlockBehaviour.Properties.copy(chestBlock.get()).explosionResistance(
                        Float.MAX_VALUE), KEYPAD_WOOD_CHEST_ENTITIES.get(woodType)::get),
                    KeypadTFChestBlockItem::new));

            KEYPAD_WOOD_DOOR_BLOCKS.put(woodType,
                MoreProtectables.block(doorName,
                    () -> new SimpleKeypadDoorBlock(BlockBehaviour.Properties.copy(doorBlock.get()).explosionResistance(
                        Float.MAX_VALUE), woodType.setType(), KEYPAD_WOOD_DOOR_ENTITIES.get(woodType)::get),
                    BlockItem::new));

            KEYPAD_WOOD_CHEST_ENTITIES.put(woodType,
                MoreProtectables.blockEntity(chestName,
                    KEYPAD_WOOD_CHEST_BLOCKS.get(woodType),
                    (pos, state) -> new KeypadTFChestBlockEntity(woodType, pos, state)));

            KEYPAD_WOOD_DOOR_ENTITIES.put(woodType,
                MoreProtectables.blockEntity(doorName,
                    KEYPAD_WOOD_DOOR_BLOCKS.get(woodType),
                    (pos, state) -> new SimpleKeypadDoorBlockEntity(KEYPAD_WOOD_DOOR_ENTITIES.get(woodType).get(),
                        pos,
                        state)));
        }
    }
}
