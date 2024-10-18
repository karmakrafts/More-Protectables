package io.karma.moreprotectables.compat.twilightforest.client.render;

import io.karma.moreprotectables.compat.twilightforest.KeypadTFChestBlockEntity;
import io.karma.moreprotectables.compat.twilightforest.TFCompatibilityContent;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import twilightforest.TwilightForestMod;

import java.util.EnumMap;
import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class KeypadTFChestRenderer extends ChestRenderer<KeypadTFChestBlockEntity> {
    public static final HashMap<Block, EnumMap<ChestType, Material>> MATERIALS = new HashMap<>();

    static {
        MATERIALS.put(TFCompatibilityContent.keypadTwilightOakChestBlock.get(), chestMaterial("twilight"));
        MATERIALS.put(TFCompatibilityContent.keypadCanopyChestBlock.get(), chestMaterial("canopy"));
        MATERIALS.put(TFCompatibilityContent.keypadMangroveChestBlock.get(), chestMaterial("mangrove"));
        MATERIALS.put(TFCompatibilityContent.keypadDarkWoodChestBlock.get(), chestMaterial("darkwood"));
        MATERIALS.put(TFCompatibilityContent.keypadTimeWoodChestBlock.get(), chestMaterial("time"));
        MATERIALS.put(TFCompatibilityContent.keypadTransformationWoodChestBlock.get(), chestMaterial("trans"));
        MATERIALS.put(TFCompatibilityContent.keypadMiningWoodChestBlock.get(), chestMaterial("mining"));
        MATERIALS.put(TFCompatibilityContent.keypadSortingWoodChestBlock.get(), chestMaterial("sort"));
    }

    public KeypadTFChestRenderer(final Context context) {
        super(context);
    }

    private static EnumMap<ChestType, Material> chestMaterial(final String type) {
        final var map = new EnumMap<ChestType, Material>(ChestType.class);
        map.put(ChestType.SINGLE,
            new Material(Sheets.CHEST_SHEET, TwilightForestMod.prefix("model/chest/" + type + "/" + type)));
        map.put(ChestType.LEFT,
            new Material(Sheets.CHEST_SHEET, TwilightForestMod.prefix("model/chest/" + type + "/left")));
        map.put(ChestType.RIGHT,
            new Material(Sheets.CHEST_SHEET, TwilightForestMod.prefix("model/chest/" + type + "/right")));
        return map;
    }

    @Override
    protected @NotNull Material getMaterial(final @NotNull KeypadTFChestBlockEntity blockEntity,
                                            final @NotNull ChestType type) {
        final var subTypes = MATERIALS.get(blockEntity.getBlockState().getBlock());
        if (subTypes == null) {
            return super.getMaterial(blockEntity, type);
        }
        return subTypes.getOrDefault(type, super.getMaterial(blockEntity, type));
    }
}