package io.karma.moreprotectables.compat.ironchest;

import com.google.auto.service.AutoService;
import com.progwml6.ironchest.client.render.IronChestRenderer;
import io.karma.moreprotectables.compat.CompatibilityModule;
import io.karma.moreprotectables.compat.CompatibilityModule.ModId;
import io.karma.moreprotectables.conversion.PasscodeConversions;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 14/10/2024
 */
@AutoService(CompatibilityModule.class)
@ModId(IronChestCompatibilityModule.MODID)
public final class IronChestCompatibilityModule implements CompatibilityModule {
    public static final String MODID = "ironchest";

    @Override
    public void init() {
        IronChestCompatibilityContent.register();

        for (final var type : IronChestCompatibilityContent.CHEST_TYPES) {
            PasscodeConversions.registerChestConversion(IronChestCompatibilityContent.CHEST.get(type),
                IronChestCompatibilityContent.KEYPAD_CHEST.get(type));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        for (final var type : IronChestCompatibilityContent.CHEST_TYPES) {
            BlockEntityRenderers.register(IronChestCompatibilityContent.KEYPAD_CHEST_ENTITY.get(type).get(),
                IronChestRenderer::new);
        }
    }

    @Override
    public void addItemsToTab(final Output output) {
        for (final var type : IronChestCompatibilityContent.CHEST_TYPES) {
            output.accept(IronChestCompatibilityContent.KEYPAD_CHEST.get(type).get());
        }
    }
}
