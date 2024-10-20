package io.karma.moreprotectables.compat.ironchest;

import com.progwml6.ironchest.common.block.IronChestsBlocks;
import com.progwml6.ironchest.common.block.IronChestsTypes;
import com.progwml6.ironchest.common.block.regular.AbstractIronChestBlock;
import com.progwml6.ironchest.common.item.IronChestBlockItem;
import io.karma.moreprotectables.MoreProtectables;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 15/10/2024
 */
public final class IronChestCompatibilityContent {
    // @formatter:off
    public static final IronChestsTypes[] CHEST_TYPES = {
        IronChestsTypes.DIRT,
        IronChestsTypes.COPPER,
        IronChestsTypes.IRON,
        IronChestsTypes.GOLD,
        IronChestsTypes.DIAMOND,
        IronChestsTypes.CRYSTAL,
        IronChestsTypes.OBSIDIAN
    };
    // @formatter:on

    public static final HashMap<IronChestsTypes, RegistryObject<? extends AbstractIronChestBlock>> CHEST_BLOCKS = new HashMap<>();
    public static final HashMap<IronChestsTypes, RegistryObject<BlockEntityType<KeypadIronChestBlockEntity>>> KEYPAD_CHEST_BLOCK_ENTITIES = new HashMap<>();
    public static final HashMap<IronChestsTypes, RegistryObject<KeypadIronChestBlock>> KEYPAD_CHEST_BLOCKS = new HashMap<>();

    static {
        CHEST_BLOCKS.put(IronChestsTypes.DIRT, IronChestsBlocks.DIRT_CHEST);
        CHEST_BLOCKS.put(IronChestsTypes.COPPER, IronChestsBlocks.COPPER_CHEST);
        CHEST_BLOCKS.put(IronChestsTypes.IRON, IronChestsBlocks.IRON_CHEST);
        CHEST_BLOCKS.put(IronChestsTypes.GOLD, IronChestsBlocks.GOLD_CHEST);
        CHEST_BLOCKS.put(IronChestsTypes.DIAMOND, IronChestsBlocks.DIAMOND_CHEST);
        CHEST_BLOCKS.put(IronChestsTypes.CRYSTAL, IronChestsBlocks.CRYSTAL_CHEST);
        CHEST_BLOCKS.put(IronChestsTypes.OBSIDIAN, IronChestsBlocks.OBSIDIAN_CHEST);
    }

    // @formatter:off
    private IronChestCompatibilityContent() {}
    // @formatter:on

    public static void register() {
        for (final var type : CHEST_TYPES) {
            final var chestName = String.format("keypad_%s_chest", type.name().toLowerCase(Locale.ROOT));

            final var keypadChestBlock = block(chestName,
                () -> new KeypadIronChestBlock(type, BlockBehaviour.Properties.copy(CHEST_BLOCKS.get(type).get())));
            KEYPAD_CHEST_BLOCKS.put(type, keypadChestBlock);

            KEYPAD_CHEST_BLOCK_ENTITIES.put(type,
                MoreProtectables.blockEntity(chestName,
                    keypadChestBlock,
                    type == IronChestsTypes.CRYSTAL ? (pos, state) -> new KeypadCrystalChestBlockEntity(type,
                        keypadChestBlock::get,
                        pos,
                        state) : (pos, state) -> new KeypadIronChestBlockEntity(type,
                        keypadChestBlock::get,
                        pos,
                        state)));
        }
    }

    private static <B extends AbstractIronChestBlock> RegistryObject<B> block(final String name,
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
