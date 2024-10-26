package io.karma.moreprotectables.compat.twilightforest;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.block.SimpleKeypadDoorBlock;
import io.karma.moreprotectables.block.SimpleKeypadTrapdoorBlock;
import io.karma.moreprotectables.blockentity.SimpleKeypadDoorBlockEntity;
import io.karma.moreprotectables.blockentity.SimpleKeypadTrapdoorBlockEntity;
import io.karma.moreprotectables.util.WoodTypeUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
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

    public static final HashMap<WoodType, RegistryObject<TFChestBlock>> WOOD_CHEST = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<DoorBlock>> WOOD_DOOR = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<TrapDoorBlock>> WOOD_TRAPDOOR = new HashMap<>();

    public static final HashMap<WoodType, RegistryObject<KeypadTFChestBlock>> KEYPAD_WOOD_CHEST = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<BlockEntityType<KeypadTFChestBlockEntity>>> KEYPAD_WOOD_CHEST_ENTITY = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<SimpleKeypadDoorBlock>> KEYPAD_WOOD_DOOR = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<BlockEntityType<SimpleKeypadDoorBlockEntity>>> KEYPAD_WOOD_DOOR_ENTITY = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<SimpleKeypadTrapdoorBlock>> KEYPAD_WOOD_TRAPDOOR = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<BlockEntityType<SimpleKeypadTrapdoorBlockEntity>>> KEYPAD_WOOD_TRAPDOOR_ENTITY = new HashMap<>();

    static {
        WOOD_CHEST.put(TFWoodTypes.TWILIGHT_OAK_WOOD_TYPE, TFBlocks.TWILIGHT_OAK_CHEST);
        WOOD_CHEST.put(TFWoodTypes.CANOPY_WOOD_TYPE, TFBlocks.CANOPY_CHEST);
        WOOD_CHEST.put(TFWoodTypes.MANGROVE_WOOD_TYPE, TFBlocks.MANGROVE_CHEST);
        WOOD_CHEST.put(TFWoodTypes.DARK_WOOD_TYPE, TFBlocks.DARK_CHEST);
        WOOD_CHEST.put(TFWoodTypes.TIME_WOOD_TYPE, TFBlocks.TIME_CHEST);
        WOOD_CHEST.put(TFWoodTypes.TRANSFORMATION_WOOD_TYPE, TFBlocks.TRANSFORMATION_CHEST);
        WOOD_CHEST.put(TFWoodTypes.MINING_WOOD_TYPE, TFBlocks.MINING_CHEST);
        WOOD_CHEST.put(TFWoodTypes.SORTING_WOOD_TYPE, TFBlocks.SORTING_CHEST);

        WOOD_DOOR.put(TFWoodTypes.TWILIGHT_OAK_WOOD_TYPE, TFBlocks.TWILIGHT_OAK_DOOR);
        WOOD_DOOR.put(TFWoodTypes.CANOPY_WOOD_TYPE, TFBlocks.CANOPY_DOOR);
        WOOD_DOOR.put(TFWoodTypes.MANGROVE_WOOD_TYPE, TFBlocks.MANGROVE_DOOR);
        WOOD_DOOR.put(TFWoodTypes.DARK_WOOD_TYPE, TFBlocks.DARK_DOOR);
        WOOD_DOOR.put(TFWoodTypes.TIME_WOOD_TYPE, TFBlocks.TIME_DOOR);
        WOOD_DOOR.put(TFWoodTypes.TRANSFORMATION_WOOD_TYPE, TFBlocks.TRANSFORMATION_DOOR);
        WOOD_DOOR.put(TFWoodTypes.MINING_WOOD_TYPE, TFBlocks.MINING_DOOR);
        WOOD_DOOR.put(TFWoodTypes.SORTING_WOOD_TYPE, TFBlocks.SORTING_DOOR);

        WOOD_TRAPDOOR.put(TFWoodTypes.TWILIGHT_OAK_WOOD_TYPE, TFBlocks.TWILIGHT_OAK_TRAPDOOR);
        WOOD_TRAPDOOR.put(TFWoodTypes.CANOPY_WOOD_TYPE, TFBlocks.CANOPY_TRAPDOOR);
        WOOD_TRAPDOOR.put(TFWoodTypes.MANGROVE_WOOD_TYPE, TFBlocks.MANGROVE_TRAPDOOR);
        WOOD_TRAPDOOR.put(TFWoodTypes.DARK_WOOD_TYPE, TFBlocks.DARK_TRAPDOOR);
        WOOD_TRAPDOOR.put(TFWoodTypes.TIME_WOOD_TYPE, TFBlocks.TIME_TRAPDOOR);
        WOOD_TRAPDOOR.put(TFWoodTypes.TRANSFORMATION_WOOD_TYPE, TFBlocks.TRANSFORMATION_TRAPDOOR);
        WOOD_TRAPDOOR.put(TFWoodTypes.MINING_WOOD_TYPE, TFBlocks.MINING_TRAPDOOR);
        WOOD_TRAPDOOR.put(TFWoodTypes.SORTING_WOOD_TYPE, TFBlocks.SORTING_TRAPDOOR);
    }

    // @formatter:off
    private TFCompatibilityContent() {}
    // @formatter:on

    public static void register() {
        for (final var woodType : WOOD_TYPES) {
            final var woodName = WoodTypeUtils.getSimpleName(woodType);
            final var chestName = String.format("keypad_tf_%s_chest", woodName);
            final var doorName = String.format("keypad_tf_%s_door", woodName);
            final var trapdoorName = String.format("keypad_tf_%s_trapdoor", woodName);

            KEYPAD_WOOD_CHEST.put(woodType,
                MoreProtectables.block(chestName,
                    () -> new KeypadTFChestBlock(BlockBehaviour.Properties.copy(WOOD_CHEST.get(woodType).get()).explosionResistance(
                        Float.MAX_VALUE), KEYPAD_WOOD_CHEST_ENTITY.get(woodType)::get),
                    KeypadTFChestBlockItem::new));

            KEYPAD_WOOD_DOOR.put(woodType,
                MoreProtectables.block(doorName,
                    () -> new SimpleKeypadDoorBlock(BlockBehaviour.Properties.copy(WOOD_DOOR.get(woodType).get()).explosionResistance(
                        Float.MAX_VALUE), woodType.setType(), KEYPAD_WOOD_DOOR_ENTITY.get(woodType)::get),
                    BlockItem::new));

            KEYPAD_WOOD_TRAPDOOR.put(woodType,
                MoreProtectables.block(trapdoorName,
                    () -> new SimpleKeypadTrapdoorBlock(BlockBehaviour.Properties.copy(WOOD_TRAPDOOR.get(woodType).get()).explosionResistance(
                        Float.MAX_VALUE), woodType.setType(), KEYPAD_WOOD_TRAPDOOR_ENTITY.get(woodType)::get),
                    BlockItem::new));

            KEYPAD_WOOD_CHEST_ENTITY.put(woodType,
                MoreProtectables.blockEntity(chestName,
                    KEYPAD_WOOD_CHEST.get(woodType),
                    (pos, state) -> new KeypadTFChestBlockEntity(woodType, pos, state)));

            KEYPAD_WOOD_DOOR_ENTITY.put(woodType,
                MoreProtectables.blockEntity(doorName,
                    KEYPAD_WOOD_DOOR.get(woodType),
                    (pos, state) -> new SimpleKeypadDoorBlockEntity(KEYPAD_WOOD_DOOR_ENTITY.get(woodType).get(),
                        pos,
                        state)));

            KEYPAD_WOOD_TRAPDOOR_ENTITY.put(woodType,
                MoreProtectables.blockEntity(trapdoorName,
                    KEYPAD_WOOD_TRAPDOOR.get(woodType),
                    (pos, state) -> new SimpleKeypadTrapdoorBlockEntity(KEYPAD_WOOD_TRAPDOOR_ENTITY.get(woodType).get(),
                        pos,
                        state)));
        }
    }
}
