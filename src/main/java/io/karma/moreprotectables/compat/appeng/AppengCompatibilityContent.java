package io.karma.moreprotectables.compat.appeng;

import appeng.block.AEBaseEntityBlock;
import appeng.block.storage.SkyChestBlock.SkyChestType;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.core.definitions.AEBlocks;
import io.karma.moreprotectables.MoreProtectables;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
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
        keypadSkyChest = MoreProtectables.block("keypad_sky_chest",
            () -> new KeypadSkyChestBlock(SkyChestType.STONE,
                BlockBehaviour.Properties.copy(AEBlocks.SKY_STONE_CHEST.block())),
            KeypadSkyChestBlockItem::new);
        keypadSmoothSkyChest = MoreProtectables.block("keypad_smooth_sky_chest",
            () -> new KeypadSkyChestBlock(SkyChestType.BLOCK,
                BlockBehaviour.Properties.copy(AEBlocks.SMOOTH_SKY_STONE_CHEST.block())),
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
}
