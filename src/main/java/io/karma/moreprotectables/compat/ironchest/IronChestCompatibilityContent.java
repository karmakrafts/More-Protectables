package io.karma.moreprotectables.compat.ironchest;

import com.progwml6.ironchest.common.block.IronChestsTypes;
import com.progwml6.ironchest.common.block.regular.AbstractIronChestBlock;
import com.progwml6.ironchest.common.block.regular.entity.AbstractIronChestBlockEntity;
import com.progwml6.ironchest.common.item.IronChestBlockItem;
import io.karma.moreprotectables.MoreProtectables;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 15/10/2024
 */
public final class IronChestCompatibilityContent {
    public static RegistryObject<BlockEntityType<KeypadIronChestBlockEntity>> keypadCopperChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadIronChestBlockEntity>> keypadIronChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadIronChestBlockEntity>> keypadGoldChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadIronChestBlockEntity>> keypadDiamondChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadIronChestBlockEntity>> keypadCrystalChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadIronChestBlockEntity>> keypadObsidianChestBlockEntity;

    public static RegistryObject<KeypadIronChestBlock> keypadCopperChestBlock;
    public static RegistryObject<KeypadIronChestBlock> keypadIronChestBlock;
    public static RegistryObject<KeypadIronChestBlock> keypadGoldChestBlock;
    public static RegistryObject<KeypadIronChestBlock> keypadDiamondChestBlock;
    public static RegistryObject<KeypadIronChestBlock> keypadCrystalChestBlock;
    public static RegistryObject<KeypadIronChestBlock> keypadObsidianChestBlock;

    // @formatter:off
    private IronChestCompatibilityContent() {}
    // @formatter:on

    public static void register() {
        final var chestBlockProps = Properties.of().mapColor(MapColor.METAL).strength(3.0F);

        keypadCopperChestBlock = chestBlock("keypad_copper_chest",
            () -> new KeypadIronChestBlock(IronChestsTypes.COPPER, chestBlockProps));
        keypadIronChestBlock = chestBlock("keypad_iron_chest",
            () -> new KeypadIronChestBlock(IronChestsTypes.IRON, chestBlockProps));
        keypadGoldChestBlock = chestBlock("keypad_gold_chest",
            () -> new KeypadIronChestBlock(IronChestsTypes.GOLD, chestBlockProps));
        keypadDiamondChestBlock = chestBlock("keypad_diamond_chest",
            () -> new KeypadIronChestBlock(IronChestsTypes.DIAMOND, chestBlockProps));
        keypadCrystalChestBlock = chestBlock("keypad_crystal_chest",
            () -> new KeypadIronChestBlock(IronChestsTypes.CRYSTAL, chestBlockProps));
        keypadObsidianChestBlock = chestBlock("keypad_obsidian_chest",
            () -> new KeypadIronChestBlock(IronChestsTypes.OBSIDIAN, chestBlockProps));

        keypadCopperChestBlockEntity = chestBlockEntity("keypad_copper_chest",
            keypadCopperChestBlock,
            (pos, state) -> new KeypadIronChestBlockEntity(IronChestsTypes.COPPER,
                keypadCopperChestBlock::get,
                pos,
                state));
        keypadIronChestBlockEntity = chestBlockEntity("keypad_iron_chest",
            keypadIronChestBlock,
            (pos, state) -> new KeypadIronChestBlockEntity(IronChestsTypes.IRON,
                keypadIronChestBlock::get,
                pos,
                state));
        keypadGoldChestBlockEntity = chestBlockEntity("keypad_gold_chest",
            keypadGoldChestBlock,
            (pos, state) -> new KeypadIronChestBlockEntity(IronChestsTypes.GOLD,
                keypadGoldChestBlock::get,
                pos,
                state));
        keypadDiamondChestBlockEntity = chestBlockEntity("keypad_diamond_chest",
            keypadDiamondChestBlock,
            (pos, state) -> new KeypadIronChestBlockEntity(IronChestsTypes.DIAMOND,
                keypadDiamondChestBlock::get,
                pos,
                state));
        keypadCrystalChestBlockEntity = chestBlockEntity("keypad_crystal_chest",
            keypadCrystalChestBlock,
            (pos, state) -> new KeypadCrystalChestBlockEntity(IronChestsTypes.CRYSTAL,
                keypadCrystalChestBlock::get,
                pos,
                state));
        keypadObsidianChestBlockEntity = chestBlockEntity("keypad_obsidian_chest",
            keypadObsidianChestBlock,
            (pos, state) -> new KeypadIronChestBlockEntity(IronChestsTypes.OBSIDIAN,
                keypadObsidianChestBlock::get,
                pos,
                state));
    }

    public static BlockEntityType<KeypadIronChestBlockEntity> getKeypadChestBlockEntityType(final IronChestsTypes type) {
        return switch (type) {
            case COPPER -> keypadCopperChestBlockEntity.get();
            case GOLD -> keypadGoldChestBlockEntity.get();
            case DIAMOND -> keypadDiamondChestBlockEntity.get();
            case CRYSTAL -> keypadCrystalChestBlockEntity.get();
            case OBSIDIAN -> keypadObsidianChestBlockEntity.get();
            default -> keypadIronChestBlockEntity.get();
        };
    }

    private static <E extends AbstractIronChestBlockEntity> RegistryObject<BlockEntityType<E>> chestBlockEntity(final String name,
                                                                                                                final Supplier<? extends Block> block,
                                                                                                                final BlockEntitySupplier<E> supplier) {
        return MoreProtectables.BLOCK_ENTITIES.register(name,
            () -> new BlockEntityType<>(supplier, Set.of(block.get()), null));
    }

    private static <B extends AbstractIronChestBlock> RegistryObject<B> chestBlock(final String name,
                                                                                   final Supplier<B> supplier) {
        final var block = MoreProtectables.BLOCKS.register(name, supplier);
        MoreProtectables.ITEMS.register(name,
            () -> new IronChestBlockItem(block.get(),
                new Item.Properties(),
                () -> () -> block.get().getType(),
                () -> () -> false));
        return block;
    }
}
