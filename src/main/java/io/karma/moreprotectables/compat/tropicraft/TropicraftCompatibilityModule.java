package io.karma.moreprotectables.compat.tropicraft;

import io.karma.moreprotectables.compat.CompatibilityModule;
import io.karma.moreprotectables.compat.CompatibilityModule.ModId;
import io.karma.moreprotectables.util.ChestPasscodeConvertible;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraftforge.fml.InterModComms;
import net.tropicraft.core.client.tileentity.BambooChestRenderer;
import net.tropicraft.core.common.block.TropicraftBlocks;

/**
 * @author Alexander Hinze
 * @since 14/10/2024
 */
@ModId(TropicraftCompatibilityModule.MODID)
public final class TropicraftCompatibilityModule implements CompatibilityModule {
    public static final String MODID = "tropicraft";

    @Override
    public void init() {
        TropicraftCompatibilityContent.register();

        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new ChestPasscodeConvertible(TropicraftBlocks.BAMBOO_CHEST.get(),
                TropicraftCompatibilityContent.keypadBambooChest.get()));
    }

    @Override
    public void initClient() {
        BlockEntityRenderers.register(TropicraftCompatibilityContent.keypadBambooChestBlockEntity.get(),
            BambooChestRenderer::new);
    }

    @Override
    public void addItemsToTab(final Output output) {
        output.accept(TropicraftCompatibilityContent.keypadBambooChest.get());
    }
}
