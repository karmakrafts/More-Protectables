package io.karma.moreprotectables.compat.twilightforest;

import io.karma.moreprotectables.client.render.DummyBlockEntityRenderer;
import io.karma.moreprotectables.compat.CompatibilityModule;
import io.karma.moreprotectables.compat.CompatibilityModule.ModId;
import io.karma.moreprotectables.compat.twilightforest.client.render.KeypadTFChestRenderer;
import io.karma.moreprotectables.util.PasscodeConversions;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

        for (final var woodType : TFCompatibilityContent.WOOD_TYPES) {
            PasscodeConversions.registerChestConversion(TFCompatibilityContent.WOOD_CHEST.get(woodType),
                TFCompatibilityContent.KEYPAD_WOOD_CHEST.get(woodType));
            PasscodeConversions.registerDoorConversion(TFCompatibilityContent.WOOD_DOOR.get(woodType),
                TFCompatibilityContent.KEYPAD_WOOD_DOOR.get(woodType));
            PasscodeConversions.registerTrapdoorConversion(TFCompatibilityContent.WOOD_TRAPDOOR.get(woodType),
                TFCompatibilityContent.KEYPAD_WOOD_TRAPDOOR.get(woodType));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        for (final var woodType : TFCompatibilityContent.WOOD_TYPES) {
            BlockEntityRenderers.register(TFCompatibilityContent.KEYPAD_WOOD_CHEST_ENTITY.get(woodType).get(),
                KeypadTFChestRenderer::new);
            BlockEntityRenderers.register(TFCompatibilityContent.KEYPAD_WOOD_DOOR_ENTITY.get(woodType).get(),
                DummyBlockEntityRenderer::new);
            BlockEntityRenderers.register(TFCompatibilityContent.KEYPAD_WOOD_TRAPDOOR_ENTITY.get(woodType).get(),
                DummyBlockEntityRenderer::new);
        }
    }

    @Override
    public void addItemsToTab(final Output output) {
        for (final var woodType : TFCompatibilityContent.WOOD_TYPES) {
            output.accept(TFCompatibilityContent.KEYPAD_WOOD_CHEST.get(woodType).get());
            output.accept(TFCompatibilityContent.KEYPAD_WOOD_DOOR.get(woodType).get());
            output.accept(TFCompatibilityContent.KEYPAD_WOOD_TRAPDOOR.get(woodType).get());
        }
    }
}
