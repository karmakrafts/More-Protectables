package io.karma.moreprotectables.compat.appeng;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseEntityBlock;
import appeng.block.storage.SkyChestBlock.SkyChestType;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import io.karma.moreprotectables.MoreProtectables;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 15/10/2024
 */
public final class AppengCompatibilityContent {
    public static RegistryObject<BlockEntityType<KeypadSkyChestBlockEntity>> keypadSkyChestBlockEntity;
    public static RegistryObject<BlockEntityType<KeypadSkyChestBlockEntity>> keypadSmoothSkyChestBlockEntity;

    public static RegistryObject<KeypadSkyChestBlock> keypadSkyChest;
    public static RegistryObject<KeypadSkyChestBlock> keypadSmoothSkyChest;

    // @formatter:off
    private AppengCompatibilityContent() {}
    // @formatter:on

    public static void register() {
        final var chestBlockProps = AEBaseBlock.stoneProps().strength(5F, 150F).noOcclusion();

        keypadSkyChest = chestBlock("keypad_sky_chest",
            () -> new KeypadSkyChestBlock(SkyChestType.STONE, chestBlockProps),
            KeypadSkyChestBlockItem::new);
        keypadSmoothSkyChest = chestBlock("keypad_smooth_sky_chest",
            () -> new KeypadSkyChestBlock(SkyChestType.BLOCK, chestBlockProps),
            KeypadSkyChestBlockItem::new);

        keypadSkyChestBlockEntity = chestBlockEntity("keypad_sky_chest",
            KeypadSkyChestBlockEntity.class,
            keypadSkyChest,
            (pos, state) -> new KeypadSkyChestBlockEntity(SkyChestType.STONE, pos, state));

        keypadSmoothSkyChestBlockEntity = chestBlockEntity("keypad_smooth_sky_chest",
            KeypadSkyChestBlockEntity.class,
            keypadSmoothSkyChest,
            (pos, state) -> new KeypadSkyChestBlockEntity(SkyChestType.BLOCK, pos, state));
    }

    private static <E extends AEBaseBlockEntity> RegistryObject<BlockEntityType<E>> chestBlockEntity(final String name,
                                                                                                     final Class<E> type,
                                                                                                     final Supplier<? extends AEBaseEntityBlock<E>> block,
                                                                                                     final BlockEntitySupplier<E> supplier) {
        return MoreProtectables.BLOCK_ENTITIES.register(name, () -> {
            final var theBlock = block.get();
            final var blockEntityType = new BlockEntityType<>(supplier, Set.of(theBlock), null);
            if (ClientTickingBlockEntity.class.isAssignableFrom(type)) {
                theBlock.setBlockEntity(type, blockEntityType, (level, pos, state, blockEntity) -> {
                    ((ClientTickingBlockEntity) blockEntity).clientTick();
                }, null);
                return blockEntityType;
            }
            theBlock.setBlockEntity(type, blockEntityType, null, null);
            return blockEntityType;
        });
    }

    private static <B extends Block> RegistryObject<B> chestBlock(final String name,
                                                                  final Supplier<B> supplier,
                                                                  final BiFunction<Block, Properties, ? extends BlockItem> itemFactory) {
        final var block = MoreProtectables.BLOCKS.register(name, supplier);
        MoreProtectables.ITEMS.register(name, () -> itemFactory.apply(block.get(), new Properties()));
        return block;
    }
}
