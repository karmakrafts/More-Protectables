package io.karma.moreprotectables.compat.ironchest;

import com.progwml6.ironchest.client.render.IronChestRenderer;
import com.progwml6.ironchest.common.block.IronChestsBlocks;
import io.karma.moreprotectables.compat.CompatibilityModule;
import io.karma.moreprotectables.compat.CompatibilityModule.ModId;
import io.karma.moreprotectables.util.ChestPasscodeConvertible;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.InterModComms;

/**
 * @author Alexander Hinze
 * @since 14/10/2024
 */
@ModId(IronChestCompatibilityModule.MODID)
public final class IronChestCompatibilityModule implements CompatibilityModule {
    public static final String MODID = "ironchest";

    @Override
    public void init() {
        IronChestCompatibilityContent.register();

        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(IronChestsBlocks.COPPER_CHEST.get(),
                IronChestCompatibilityContent.keypadCopperChestBlock.get()));
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(IronChestsBlocks.IRON_CHEST.get(),
                IronChestCompatibilityContent.keypadIronChestBlock.get()));
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(IronChestsBlocks.GOLD_CHEST.get(),
                IronChestCompatibilityContent.keypadGoldChestBlock.get()));
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(IronChestsBlocks.DIAMOND_CHEST.get(),
                IronChestCompatibilityContent.keypadDiamondChestBlock.get()));
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(IronChestsBlocks.CRYSTAL_CHEST.get(),
                IronChestCompatibilityContent.keypadCrystalChestBlock.get()));
        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(IronChestsBlocks.OBSIDIAN_CHEST.get(),
                IronChestCompatibilityContent.keypadObsidianChestBlock.get()));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        BlockEntityRenderers.register(IronChestCompatibilityContent.keypadCopperChestBlockEntity.get(),
            IronChestRenderer::new);
        BlockEntityRenderers.register(IronChestCompatibilityContent.keypadIronChestBlockEntity.get(),
            IronChestRenderer::new);
        BlockEntityRenderers.register(IronChestCompatibilityContent.keypadGoldChestBlockEntity.get(),
            IronChestRenderer::new);
        BlockEntityRenderers.register(IronChestCompatibilityContent.keypadDiamondChestBlockEntity.get(),
            IronChestRenderer::new);
        BlockEntityRenderers.register(IronChestCompatibilityContent.keypadCrystalChestBlockEntity.get(),
            IronChestRenderer::new);
        BlockEntityRenderers.register(IronChestCompatibilityContent.keypadObsidianChestBlockEntity.get(),
            IronChestRenderer::new);
    }

    @Override
    public void addItemsToTab(final Output output) {
        output.accept(IronChestCompatibilityContent.keypadCopperChestBlock.get());
        output.accept(IronChestCompatibilityContent.keypadIronChestBlock.get());
        output.accept(IronChestCompatibilityContent.keypadGoldChestBlock.get());
        output.accept(IronChestCompatibilityContent.keypadDiamondChestBlock.get());
        output.accept(IronChestCompatibilityContent.keypadCrystalChestBlock.get());
        output.accept(IronChestCompatibilityContent.keypadObsidianChestBlock.get());
    }
}
