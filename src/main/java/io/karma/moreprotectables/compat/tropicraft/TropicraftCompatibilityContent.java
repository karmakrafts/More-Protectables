package io.karma.moreprotectables.compat.tropicraft;

import io.karma.moreprotectables.MoreProtectables;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;
import net.tropicraft.core.common.block.tileentity.BambooChestBlockEntity;

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
        keypadBambooChest = MoreProtectables.block("keypad_bamboo_chest",
            () -> new KeypadBambooChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.BAMBOO).strength(
                0.2F,
                5.0F)),
            KeypadBambooChestBlockItem::new);

        keypadBambooChestBlockEntity = MoreProtectables.blockEntity("keypad_bamboo_chest",
            keypadBambooChest,
            (pos, state) -> new KeypadBambooChestBlockEntity(keypadBambooChestBlockEntity.get(), pos, state));
    }
}
