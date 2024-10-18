package io.karma.moreprotectables.compat.twilightforest;

import io.karma.moreprotectables.compat.CompatibilityModule;
import io.karma.moreprotectables.compat.CompatibilityModule.ModId;
import io.karma.moreprotectables.compat.twilightforest.client.render.KeypadTFChestRenderer;
import io.karma.moreprotectables.util.ChestPasscodeConvertible;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.InterModComms;
import twilightforest.init.TFBlocks;

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
@ModId(TFCompatibilityModule.MODID)
public final class TFCompatibilityModule implements CompatibilityModule {
    public static final String MODID = "twilightforest";

    @Override
    public void init() {
        TFCompatibilityContent.register();

        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(TFBlocks.TWILIGHT_OAK_CHEST.get(),
                TFCompatibilityContent.keypadTwilightOakChestBlock.get()));
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(TFBlocks.CANOPY_CHEST.get(),
                TFCompatibilityContent.keypadCanopyChestBlock.get()));
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(TFBlocks.MANGROVE_CHEST.get(),
                TFCompatibilityContent.keypadMangroveChestBlock.get()));
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(TFBlocks.DARK_CHEST.get(),
                TFCompatibilityContent.keypadDarkWoodChestBlock.get()));
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(TFBlocks.TIME_CHEST.get(),
                TFCompatibilityContent.keypadTimeWoodChestBlock.get()));
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(TFBlocks.TRANSFORMATION_CHEST.get(),
                TFCompatibilityContent.keypadTransformationWoodChestBlock.get()));
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(TFBlocks.MINING_CHEST.get(),
                TFCompatibilityContent.keypadMiningWoodChestBlock.get()));
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(TFBlocks.SORTING_CHEST.get(),
                TFCompatibilityContent.keypadSortingWoodChestBlock.get()));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        BlockEntityRenderers.register(TFCompatibilityContent.keypadTwilightOakChestBlockEntity.get(),
            KeypadTFChestRenderer::new);
        BlockEntityRenderers.register(TFCompatibilityContent.keypadCanopyChestBlockEntity.get(),
            KeypadTFChestRenderer::new);
        BlockEntityRenderers.register(TFCompatibilityContent.keypadMangroveChestBlockEntity.get(),
            KeypadTFChestRenderer::new);
        BlockEntityRenderers.register(TFCompatibilityContent.keypadDarkWoodChestBlockEntity.get(),
            KeypadTFChestRenderer::new);
        BlockEntityRenderers.register(TFCompatibilityContent.keypadTimeWoodChestBlockEntity.get(),
            KeypadTFChestRenderer::new);
        BlockEntityRenderers.register(TFCompatibilityContent.keypadTransformationWoodChestBlockEntity.get(),
            KeypadTFChestRenderer::new);
        BlockEntityRenderers.register(TFCompatibilityContent.keypadMiningWoodChestBlockEntity.get(),
            KeypadTFChestRenderer::new);
        BlockEntityRenderers.register(TFCompatibilityContent.keypadSortingWoodChestBlockEntity.get(),
            KeypadTFChestRenderer::new);
    }

    @Override
    public void addItemsToTab(final Output output) {
        output.accept(TFCompatibilityContent.keypadTwilightOakChestBlock.get());
        output.accept(TFCompatibilityContent.keypadCanopyChestBlock.get());
        output.accept(TFCompatibilityContent.keypadMangroveChestBlock.get());
        output.accept(TFCompatibilityContent.keypadDarkWoodChestBlock.get());
        output.accept(TFCompatibilityContent.keypadTimeWoodChestBlock.get());
        output.accept(TFCompatibilityContent.keypadTransformationWoodChestBlock.get());
        output.accept(TFCompatibilityContent.keypadMiningWoodChestBlock.get());
        output.accept(TFCompatibilityContent.keypadSortingWoodChestBlock.get());
    }
}
