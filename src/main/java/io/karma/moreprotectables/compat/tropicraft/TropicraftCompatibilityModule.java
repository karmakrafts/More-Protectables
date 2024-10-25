package io.karma.moreprotectables.compat.tropicraft;

import io.karma.moreprotectables.client.render.DummyBlockEntityRenderer;
import io.karma.moreprotectables.compat.CompatibilityModule;
import io.karma.moreprotectables.compat.CompatibilityModule.ModId;
import io.karma.moreprotectables.util.PasscodeConversions;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.tropicraft.core.client.tileentity.BambooChestRenderer;
import net.tropicraft.core.common.block.TropicraftBlocks;

/**
 * @author Alexander Hinze
 * @since 14/10/2024
 */
@ModId(TropicraftCompatibilityModule.MODID)
public final class TropicraftCompatibilityModule implements CompatibilityModule {
    public static final String MODID = "tropicraft";

    @SuppressWarnings("all")
    @Override
    public void init() {
        TropicraftCompatibilityContent.register();

        PasscodeConversions.registerChestConversion(() -> TropicraftBlocks.BAMBOO_CHEST.get(),
            TropicraftCompatibilityContent.keypadBambooChest);
        PasscodeConversions.registerDoorConversion(() -> TropicraftBlocks.BAMBOO_DOOR.get(),
            TropicraftCompatibilityContent.keypadBambooDoor);
        PasscodeConversions.registerTrapdoorConversion(() -> TropicraftBlocks.BAMBOO_TRAPDOOR.get(),
            TropicraftCompatibilityContent.keypadBambooTrapdoor);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void initClient() {
        BlockEntityRenderers.register(TropicraftCompatibilityContent.keypadBambooChestEntity.get(),
            BambooChestRenderer::new);
        BlockEntityRenderers.register(TropicraftCompatibilityContent.keypadBambooDoorEntity.get(),
            DummyBlockEntityRenderer::new);
        BlockEntityRenderers.register(TropicraftCompatibilityContent.keypadBambooTrapdoorEntity.get(),
            DummyBlockEntityRenderer::new);
        ItemBlockRenderTypes.setRenderLayer(TropicraftCompatibilityContent.keypadBambooDoor.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(TropicraftCompatibilityContent.keypadBambooTrapdoor.get(),
            RenderType.cutout());
    }

    @Override
    public void addItemsToTab(final Output output) {
        output.accept(TropicraftCompatibilityContent.keypadBambooChest.get());
        output.accept(TropicraftCompatibilityContent.keypadBambooDoor.get());
        output.accept(TropicraftCompatibilityContent.keypadBambooTrapdoor.get());
    }
}
