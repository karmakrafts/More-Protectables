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
import net.tropicraft.core.common.block.TropicraftWoodTypes;

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
        for (final var woodType : TropicraftCompatibilityContent.WOOD_TYPES) {
            PasscodeConversions.registerDoorConversion(TropicraftCompatibilityContent.WOOD_DOOR.get(woodType),
                TropicraftCompatibilityContent.KEYPAD_WOOD_DOOR.get(woodType));
            PasscodeConversions.registerTrapdoorConversion(TropicraftCompatibilityContent.WOOD_TRAPDOOR.get(woodType),
                TropicraftCompatibilityContent.KEYPAD_WOOD_TRAPDOOR.get(woodType));
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void initClient() {
        BlockEntityRenderers.register(TropicraftCompatibilityContent.keypadBambooChestEntity.get(),
            BambooChestRenderer::new);
        for (final var woodType : TropicraftCompatibilityContent.WOOD_TYPES) {
            BlockEntityRenderers.register(TropicraftCompatibilityContent.KEYPAD_WOOD_DOOR_ENTITY.get(woodType).get(),
                DummyBlockEntityRenderer::new);
            BlockEntityRenderers.register(TropicraftCompatibilityContent.KEYPAD_WOOD_TRAPDOOR_ENTITY.get(woodType).get(),
                DummyBlockEntityRenderer::new);
            ItemBlockRenderTypes.setRenderLayer(TropicraftCompatibilityContent.KEYPAD_WOOD_DOOR.get(woodType).get(),
                RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(TropicraftCompatibilityContent.KEYPAD_WOOD_TRAPDOOR.get(woodType).get(),
                RenderType.cutout());
        }
    }

    @Override
    public void addItemsToTab(final Output output) {
        for (final var woodType : TropicraftCompatibilityContent.WOOD_TYPES) {
            if (woodType == TropicraftWoodTypes.BAMBOO) {
                output.accept(TropicraftCompatibilityContent.keypadBambooChest.get());
            }
            output.accept(TropicraftCompatibilityContent.KEYPAD_WOOD_DOOR.get(woodType).get());
            output.accept(TropicraftCompatibilityContent.KEYPAD_WOOD_TRAPDOOR.get(woodType).get());
        }
    }
}
