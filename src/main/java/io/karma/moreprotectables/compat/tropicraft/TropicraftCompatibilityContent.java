package io.karma.moreprotectables.compat.tropicraft;

import io.karma.moreprotectables.MoreProtectables;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;
import net.tropicraft.core.common.block.tileentity.BambooChestBlockEntity;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public final class TropicraftCompatibilityContent {
    public static RegistryObject<Block> keypadBambooChest;
    public static RegistryObject<BlockEntityType<BambooChestBlockEntity>> keypadBambooChestBlockEntity;

    // @formatter:off
    private TropicraftCompatibilityContent() {}
    // @formatter:on

    public static void register() {
        keypadBambooChest = block("keypad_bamboo_chest",
            () -> new KeypadBambooChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.BAMBOO).strength(
                0.2F,
                5.0F)),
            KeypadBambooChestBlockItem::new);

        keypadBambooChestBlockEntity = blockEntity("keypad_bamboo_chest",
            keypadBambooChest,
            (pos, state) -> new KeypadBambooChestBlockEntity(keypadBambooChestBlockEntity.get(), pos, state));
    }

    private static <E extends BlockEntity> RegistryObject<BlockEntityType<E>> blockEntity(final String name,
                                                                                          final Supplier<? extends Block> block,
                                                                                          final BlockEntitySupplier<E> supplier) {
        return MoreProtectables.BLOCK_ENTITIES.register(name,
            () -> new BlockEntityType<>(supplier, Set.of(block.get()), null));
    }

    private static <B extends Block> RegistryObject<B> block(final String name,
                                                             final Supplier<B> factory,
                                                             final BiFunction<Block, Properties, ? extends BlockItem> itemFactory) {
        final var block = MoreProtectables.BLOCKS.register(name, factory);
        MoreProtectables.ITEMS.register(name, () -> itemFactory.apply(block.get(), new Item.Properties()));
        return block;
    }
}
