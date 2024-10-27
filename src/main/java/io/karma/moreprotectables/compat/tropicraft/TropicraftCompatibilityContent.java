package io.karma.moreprotectables.compat.tropicraft;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.block.SimpleKeypadDoorBlock;
import io.karma.moreprotectables.block.SimpleKeypadTrapdoorBlock;
import io.karma.moreprotectables.blockentity.SimpleKeypadDoorBlockEntity;
import io.karma.moreprotectables.blockentity.SimpleKeypadTrapdoorBlockEntity;
import io.karma.moreprotectables.item.SimpleKeypadBlockItem;
import io.karma.moreprotectables.util.WoodTypeUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.registries.RegistryObject;
import net.tropicraft.core.common.block.TropicraftBlocks;
import net.tropicraft.core.common.block.TropicraftWoodTypes;
import net.tropicraft.core.common.block.tileentity.BambooChestBlockEntity;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public final class TropicraftCompatibilityContent {
    // @formatter:off
    public static final WoodType[] WOOD_TYPES = {
        TropicraftWoodTypes.MAHOGANY,
        TropicraftWoodTypes.PALM,
        TropicraftWoodTypes.BAMBOO,
        TropicraftWoodTypes.THATCH,
        TropicraftWoodTypes.MANGROVE
    };
    // @formatter:on

    public static final HashMap<WoodType, Supplier<DoorBlock>> WOOD_DOOR = new HashMap<>();
    public static final HashMap<WoodType, Supplier<TrapDoorBlock>> WOOD_TRAPDOOR = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<SimpleKeypadDoorBlock>> KEYPAD_WOOD_DOOR = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<SimpleKeypadTrapdoorBlock>> KEYPAD_WOOD_TRAPDOOR = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<BlockEntityType<SimpleKeypadDoorBlockEntity>>> KEYPAD_WOOD_DOOR_ENTITY = new HashMap<>();
    public static final HashMap<WoodType, RegistryObject<BlockEntityType<SimpleKeypadTrapdoorBlockEntity>>> KEYPAD_WOOD_TRAPDOOR_ENTITY = new HashMap<>();
    public static RegistryObject<Block> keypadBambooChest;
    public static RegistryObject<BlockEntityType<BambooChestBlockEntity>> keypadBambooChestEntity;

    // Ignore the warnings on these as we force a lazy classloading indirection
    static {
        WOOD_DOOR.put(TropicraftWoodTypes.MAHOGANY, () -> TropicraftBlocks.MAHOGANY_DOOR.get());
        WOOD_DOOR.put(TropicraftWoodTypes.PALM, () -> TropicraftBlocks.PALM_DOOR.get());
        WOOD_DOOR.put(TropicraftWoodTypes.BAMBOO, () -> TropicraftBlocks.BAMBOO_DOOR.get());
        WOOD_DOOR.put(TropicraftWoodTypes.THATCH, () -> TropicraftBlocks.THATCH_DOOR.get());
        WOOD_DOOR.put(TropicraftWoodTypes.MANGROVE, () -> TropicraftBlocks.MANGROVE_DOOR.get());

        WOOD_TRAPDOOR.put(TropicraftWoodTypes.MAHOGANY, () -> TropicraftBlocks.MAHOGANY_TRAPDOOR.get());
        WOOD_TRAPDOOR.put(TropicraftWoodTypes.PALM, () -> TropicraftBlocks.PALM_TRAPDOOR.get());
        WOOD_TRAPDOOR.put(TropicraftWoodTypes.BAMBOO, () -> TropicraftBlocks.BAMBOO_TRAPDOOR.get());
        WOOD_TRAPDOOR.put(TropicraftWoodTypes.THATCH, () -> TropicraftBlocks.THATCH_TRAPDOOR.get());
        WOOD_TRAPDOOR.put(TropicraftWoodTypes.MANGROVE, () -> TropicraftBlocks.MANGROVE_TRAPDOOR.get());
    }

    // @formatter:off
    private TropicraftCompatibilityContent() {}
    // @formatter:on

    public static void register() {
        keypadBambooChest = MoreProtectables.block("keypad_tropical_bamboo_chest",
            () -> new KeypadBambooChestBlock(BlockBehaviour.Properties.copy(TropicraftBlocks.BAMBOO_CHEST.get()).explosionResistance(
                Float.MAX_VALUE)),
            KeypadBambooChestBlockItem::new);

        keypadBambooChestEntity = MoreProtectables.blockEntity("keypad_tropical_bamboo_chest",
            keypadBambooChest,
            (pos, state) -> new KeypadBambooChestBlockEntity(keypadBambooChestEntity.get(), pos, state));

        for (final var woodType : WOOD_TYPES) {
            final var woodName = WoodTypeUtils.getSimpleName(woodType);
            final var doorName = String.format("keypad_tropical_%s_door", woodName);
            final var trapdoorName = String.format("keypad_tropical_%s_trapdoor", woodName);

            KEYPAD_WOOD_DOOR.put(woodType,
                MoreProtectables.block(doorName,
                    () -> new SimpleKeypadDoorBlock(BlockBehaviour.Properties.copy(TropicraftBlocks.BAMBOO_DOOR.get()).explosionResistance(
                        Float.MAX_VALUE),
                        TropicraftWoodTypes.BAMBOO.setType(),
                        KEYPAD_WOOD_DOOR_ENTITY.get(woodType)::get),
                    BlockItem::new));

            KEYPAD_WOOD_TRAPDOOR.put(woodType,
                MoreProtectables.block(trapdoorName,
                    () -> new SimpleKeypadTrapdoorBlock(BlockBehaviour.Properties.copy(TropicraftBlocks.BAMBOO_TRAPDOOR.get()).explosionResistance(
                        Float.MAX_VALUE),
                        TropicraftWoodTypes.BAMBOO.setType(),
                        KEYPAD_WOOD_TRAPDOOR_ENTITY.get(woodType)::get),
                    SimpleKeypadBlockItem::new));

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
