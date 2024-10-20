package io.karma.moreprotectables.compat.tropicraft;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.blockentity.SimpleKeypadDoorBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;
import net.tropicraft.core.common.block.TropicraftBlocks;
import net.tropicraft.core.common.block.TropicraftWoodTypes;
import net.tropicraft.core.common.block.tileentity.BambooChestBlockEntity;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public final class TropicraftCompatibilityContent {
    public static RegistryObject<Block> keypadBambooChest;
    public static RegistryObject<Block> keypadBambooDoor;

    public static RegistryObject<BlockEntityType<BambooChestBlockEntity>> keypadBambooChestBlockEntity;
    public static RegistryObject<BlockEntityType<SimpleKeypadDoorBlockEntity>> keypadBambooDoorBlockEntity;

    // @formatter:off
    private TropicraftCompatibilityContent() {}
    // @formatter:on

    public static void register() {
        keypadBambooChest = MoreProtectables.block("keypad_tropical_bamboo_chest",
            () -> new KeypadBambooChestBlock(BlockBehaviour.Properties.copy(TropicraftBlocks.BAMBOO_CHEST.get()).explosionResistance(
                Float.MAX_VALUE)),
            KeypadBambooChestBlockItem::new);

        keypadBambooDoor = MoreProtectables.block("keypad_tropical_bamboo_door",
            () -> new KeypadBambooDoorBlock(BlockBehaviour.Properties.copy(TropicraftBlocks.BAMBOO_DOOR.get()).explosionResistance(
                Float.MAX_VALUE), TropicraftWoodTypes.BAMBOO.setType()),
            BlockItem::new);

        keypadBambooChestBlockEntity = MoreProtectables.blockEntity("keypad_tropical_bamboo_chest",
            keypadBambooChest,
            (pos, state) -> new KeypadBambooChestBlockEntity(keypadBambooChestBlockEntity.get(), pos, state));

        keypadBambooDoorBlockEntity = MoreProtectables.blockEntity("keypad_tropical_bamboo_door",
            keypadBambooDoor,
            (pos, state) -> new SimpleKeypadDoorBlockEntity(keypadBambooDoorBlockEntity.get(), pos, state));
    }
}
