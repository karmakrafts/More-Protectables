package io.karma.moreprotectables.compat.ironchest;

import com.progwml6.ironchest.client.render.IronChestRenderer;
import io.karma.moreprotectables.compat.CompatibilityModule;
import io.karma.moreprotectables.compat.CompatibilityModule.ModId;
import io.karma.moreprotectables.util.KeypadChestConvertible;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.InterModComms;

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

        for (final var type : IronChestCompatibilityContent.CHEST_TYPES) {
            InterModComms.sendTo(SecurityCraft.MODID,
                SecurityCraftAPI.IMC_PASSCODE_CONVERTIBLE_MSG,
                () -> new KeypadChestConvertible(IronChestCompatibilityContent.CHEST_BLOCKS.get(type).get(),
                    IronChestCompatibilityContent.KEYPAD_CHEST_BLOCKS.get(type).get()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        for (final var type : IronChestCompatibilityContent.CHEST_TYPES) {
            BlockEntityRenderers.register(IronChestCompatibilityContent.KEYPAD_CHEST_BLOCK_ENTITIES.get(type).get(),
                IronChestRenderer::new);
        }
    }

    @Override
    public void addItemsToTab(final Output output) {
        for (final var type : IronChestCompatibilityContent.CHEST_TYPES) {
            output.accept(IronChestCompatibilityContent.KEYPAD_CHEST_BLOCKS.get(type).get());
        }
    }
}
