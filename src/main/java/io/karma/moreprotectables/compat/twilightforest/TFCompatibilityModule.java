package io.karma.moreprotectables.compat.twilightforest;

import io.karma.moreprotectables.client.render.DummyBlockEntityRenderer;
import io.karma.moreprotectables.compat.CompatibilityModule;
import io.karma.moreprotectables.compat.CompatibilityModule.ModId;
import io.karma.moreprotectables.compat.twilightforest.client.render.KeypadTFChestRenderer;
import io.karma.moreprotectables.util.KeypadChestConvertible;
import io.karma.moreprotectables.util.KeypadDoorConvertible;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.InterModComms;

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
            InterModComms.sendTo(SecurityCraft.MODID,
                SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
                () -> new KeypadChestConvertible(TFCompatibilityContent.WOOD_CHEST_BLOCKS.get(woodType).get(),
                    TFCompatibilityContent.KEYPAD_WOOD_CHEST_BLOCKS.get(woodType).get()));
            InterModComms.sendTo(SecurityCraft.MODID,
                SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
                () -> new KeypadDoorConvertible(TFCompatibilityContent.WOOD_DOOR_BLOCKS.get(woodType).get(),
                    TFCompatibilityContent.KEYPAD_WOOD_DOOR_BLOCKS.get(woodType).get()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        for (final var woodType : TFCompatibilityContent.WOOD_TYPES) {
            BlockEntityRenderers.register(TFCompatibilityContent.KEYPAD_WOOD_CHEST_ENTITIES.get(woodType).get(),
                KeypadTFChestRenderer::new);
            BlockEntityRenderers.register(TFCompatibilityContent.KEYPAD_WOOD_DOOR_ENTITIES.get(woodType).get(),
                DummyBlockEntityRenderer::new);
        }
    }

    @Override
    public void addItemsToTab(final Output output) {
        for (final var woodType : TFCompatibilityContent.WOOD_TYPES) {
            output.accept(TFCompatibilityContent.KEYPAD_WOOD_CHEST_BLOCKS.get(woodType).get());
            output.accept(TFCompatibilityContent.KEYPAD_WOOD_DOOR_BLOCKS.get(woodType).get());
        }
    }
}
