package io.karma.moreprotectables.compat.ironchest;

import com.progwml6.ironchest.client.render.IronChestRenderer;
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
@ModId(IronChestCompatibilityModule.MODID)
public final class IronChestCompatibilityModule implements CompatibilityModule {
    public static final String MODID = "ironchest";

    @Override
    public void init() {
        IronChestCompatibilityContent.register();
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
