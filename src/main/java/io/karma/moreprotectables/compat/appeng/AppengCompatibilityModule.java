package io.karma.moreprotectables.compat.appeng;

import appeng.client.render.tesr.SkyChestTESR;
import io.karma.moreprotectables.compat.CompatibilityModule;
import io.karma.moreprotectables.compat.CompatibilityModule.ModId;
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
        BlockEntityRenderers.register(AppengCompatibilityContent.keypadSkyChestBlockEntity.get(), SkyChestTESR::new);
        BlockEntityRenderers.register(AppengCompatibilityContent.keypadSmoothSkyChestBlockEntity.get(),
            SkyChestTESR::new);
    }

    @Override
    public void addItemsToTab(final Output output) {
        output.accept(AppengCompatibilityContent.keypadSkyChest.get());
        output.accept(AppengCompatibilityContent.keypadSmoothSkyChest.get());
    }
}
