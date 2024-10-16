package io.karma.moreprotectables.compat.appeng;

import io.karma.moreprotectables.compat.CompatibilityModule;
import io.karma.moreprotectables.compat.CompatibilityModule.ModId;
import io.karma.moreprotectables.compat.appeng.client.render.KeypadSkyChestRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 14/10/2024
 */
@ModId(AppengCompatibilityModule.MODID)
public final class AppengCompatibilityModule implements CompatibilityModule {
    public static final String MODID = "ae2";

    @Override
    public void init() {
        AppengCompatibilityContent.register();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        BlockEntityRenderers.register(AppengCompatibilityContent.keypadSkyChestBlockEntity.get(),
            KeypadSkyChestRenderer::new);
        BlockEntityRenderers.register(AppengCompatibilityContent.keypadSmoothSkyChestBlockEntity.get(),
            KeypadSkyChestRenderer::new);
    }

    @Override
    public void addItemsToTab(final Output output) {
        output.accept(AppengCompatibilityContent.keypadSkyChest.get());
        output.accept(AppengCompatibilityContent.keypadSmoothSkyChest.get());
    }
}
