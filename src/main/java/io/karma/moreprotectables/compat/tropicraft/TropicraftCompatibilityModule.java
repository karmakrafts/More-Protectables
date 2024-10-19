package io.karma.moreprotectables.compat.tropicraft;

import io.karma.moreprotectables.client.render.DummyBlockEntityRenderer;
import io.karma.moreprotectables.compat.CompatibilityModule;
import io.karma.moreprotectables.compat.CompatibilityModule.ModId;
import io.karma.moreprotectables.util.KeypadChestConvertible;
import io.karma.moreprotectables.util.KeypadDoorConvertible;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
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
            () -> new KeypadChestConvertible(TropicraftBlocks.BAMBOO_CHEST.get(),
                TropicraftCompatibilityContent.keypadBambooChest.get()));

        InterModComms.sendTo(SecurityCraft.MODID,
            SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
            () -> new KeypadDoorConvertible(TropicraftBlocks.BAMBOO_DOOR.get(),
                TropicraftCompatibilityContent.keypadBambooDoor.get()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void initClient() {
        BlockEntityRenderers.register(TropicraftCompatibilityContent.keypadBambooChestBlockEntity.get(),
            BambooChestRenderer::new);
        BlockEntityRenderers.register(TropicraftCompatibilityContent.keypadBambooDoorBlockEntity.get(),
            DummyBlockEntityRenderer::new);
        ItemBlockRenderTypes.setRenderLayer(TropicraftCompatibilityContent.keypadBambooDoor.get(), RenderType.cutout());
    }

    @Override
    public void addItemsToTab(final Output output) {
        output.accept(TropicraftCompatibilityContent.keypadBambooChest.get());
        output.accept(TropicraftCompatibilityContent.keypadBambooDoor.get());
    }
}
