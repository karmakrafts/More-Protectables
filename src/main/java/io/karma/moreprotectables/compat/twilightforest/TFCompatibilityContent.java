package io.karma.moreprotectables.compat.twilightforest;

import io.karma.moreprotectables.MoreProtectables;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;
import twilightforest.init.TFBlocks;
import twilightforest.util.TFWoodTypes;

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
public final class TFCompatibilityContent {
    public static RegistryObject<KeypadTFChestBlock> keypadTwilightOakChestBlock;
    public static RegistryObject<KeypadTFChestBlock> keypadCanopyChestBlock;
    public static RegistryObject<KeypadTFChestBlock> keypadMangroveChestBlock;
    public static RegistryObject<KeypadTFChestBlock> keypadDarkWoodChestBlock;
    public static RegistryObject<KeypadTFChestBlock> keypadTimeWoodChestBlock;
    public static RegistryObject<KeypadTFChestBlock> keypadTransformationWoodChestBlock;
    public static RegistryObject<KeypadTFChestBlock> keypadMiningWoodChestBlock;
    public static RegistryObject<KeypadTFChestBlock> keypadSortingWoodChestBlock;

    public static RegistryObject<BlockEntityType<KeypadTFChestBlockEntity>> keypadTwilightOakChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadTFChestBlockEntity>> keypadCanopyChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadTFChestBlockEntity>> keypadMangroveChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadTFChestBlockEntity>> keypadDarkWoodChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadTFChestBlockEntity>> keypadTimeWoodChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadTFChestBlockEntity>> keypadTransformationWoodChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadTFChestBlockEntity>> keypadMiningWoodChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadTFChestBlockEntity>> keypadSortingWoodChestBlockEntity;

    // @formatter:off
    private TFCompatibilityContent() {}
    // @formatter:on

    public static void register() {
        keypadTwilightOakChestBlock = MoreProtectables.block("keypad_twilight_oak_chest",
            () -> new KeypadTFChestBlock(BlockBehaviour.Properties.copy(TFBlocks.TWILIGHT_OAK_PLANKS.get()),
                keypadTwilightOakChestBlockEntity::get),
            KeypadTFChestBlockItem::new);
        keypadCanopyChestBlock = MoreProtectables.block("keypad_canopy_chest",
            () -> new KeypadTFChestBlock(BlockBehaviour.Properties.copy(TFBlocks.CANOPY_PLANKS.get()),
                keypadCanopyChestBlockEntity::get),
            KeypadTFChestBlockItem::new);
        keypadMangroveChestBlock = MoreProtectables.block("keypad_mangrove_chest",
            () -> new KeypadTFChestBlock(BlockBehaviour.Properties.copy(TFBlocks.MANGROVE_PLANKS.get()),
                keypadMangroveChestBlockEntity::get),
            KeypadTFChestBlockItem::new);
        keypadDarkWoodChestBlock = MoreProtectables.block("keypad_dark_wood_chest",
            () -> new KeypadTFChestBlock(BlockBehaviour.Properties.copy(TFBlocks.DARK_PLANKS.get()),
                keypadDarkWoodChestBlockEntity::get),
            KeypadTFChestBlockItem::new);
        keypadTimeWoodChestBlock = MoreProtectables.block("keypad_time_wood_chest",
            () -> new KeypadTFChestBlock(BlockBehaviour.Properties.copy(TFBlocks.TIME_PLANKS.get()),
                keypadTimeWoodChestBlockEntity::get),
            KeypadTFChestBlockItem::new);
        keypadTransformationWoodChestBlock = MoreProtectables.block("keypad_transformation_wood_chest",
            () -> new KeypadTFChestBlock(BlockBehaviour.Properties.copy(TFBlocks.TRANSFORMATION_PLANKS.get()),
                keypadTransformationWoodChestBlockEntity::get),
            KeypadTFChestBlockItem::new);
        keypadMiningWoodChestBlock = MoreProtectables.block("keypad_mining_wood_chest",
            () -> new KeypadTFChestBlock(BlockBehaviour.Properties.copy(TFBlocks.MINING_PLANKS.get()),
                keypadMiningWoodChestBlockEntity::get),
            KeypadTFChestBlockItem::new);
        keypadSortingWoodChestBlock = MoreProtectables.block("keypad_sorting_wood_chest",
            () -> new KeypadTFChestBlock(BlockBehaviour.Properties.copy(TFBlocks.SORTING_PLANKS.get()),
                keypadSortingWoodChestBlockEntity::get),
            KeypadTFChestBlockItem::new);

        keypadTwilightOakChestBlockEntity = MoreProtectables.blockEntity("keypad_twilight_oak_chest",
            keypadTwilightOakChestBlock,
            (pos, state) -> new KeypadTFChestBlockEntity(TFWoodTypes.TWILIGHT_OAK_WOOD_TYPE, pos, state));
        keypadCanopyChestBlockEntity = MoreProtectables.blockEntity("keypad_canopy_chest",
            keypadCanopyChestBlock,
            (pos, state) -> new KeypadTFChestBlockEntity(TFWoodTypes.CANOPY_WOOD_TYPE, pos, state));
        keypadMangroveChestBlockEntity = MoreProtectables.blockEntity("keypad_mangrove_chest",
            keypadMangroveChestBlock,
            (pos, state) -> new KeypadTFChestBlockEntity(TFWoodTypes.MANGROVE_WOOD_TYPE, pos, state));
        keypadDarkWoodChestBlockEntity = MoreProtectables.blockEntity("keypad_dark_wood_chest",
            keypadDarkWoodChestBlock,
            (pos, state) -> new KeypadTFChestBlockEntity(TFWoodTypes.DARK_WOOD_TYPE, pos, state));
        keypadTimeWoodChestBlockEntity = MoreProtectables.blockEntity("keypad_time_wood_chest",
            keypadTimeWoodChestBlock,
            (pos, state) -> new KeypadTFChestBlockEntity(TFWoodTypes.TIME_WOOD_TYPE, pos, state));
        keypadTransformationWoodChestBlockEntity = MoreProtectables.blockEntity("keypad_transformation_wood_chest",
            keypadTransformationWoodChestBlock,
            (pos, state) -> new KeypadTFChestBlockEntity(TFWoodTypes.TRANSFORMATION_WOOD_TYPE, pos, state));
        keypadMiningWoodChestBlockEntity = MoreProtectables.blockEntity("keypad_mining_wood_chest",
            keypadMiningWoodChestBlock,
            (pos, state) -> new KeypadTFChestBlockEntity(TFWoodTypes.MINING_WOOD_TYPE, pos, state));
        keypadSortingWoodChestBlockEntity = MoreProtectables.blockEntity("keypad_sorting_wood_chest",
            keypadSortingWoodChestBlock,
            (pos, state) -> new KeypadTFChestBlockEntity(TFWoodTypes.SORTING_WOOD_TYPE, pos, state));
    }
}
