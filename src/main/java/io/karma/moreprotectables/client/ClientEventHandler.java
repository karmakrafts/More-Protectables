package io.karma.moreprotectables.client;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.util.WoodTypeUtils;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * @author Alexander Hinze
 * @since 20/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientEventHandler {
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();

    // @formatter:off
    private ClientEventHandler() {}
    // @formatter:on

    @Internal
    public void setup() {
        final var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onRegisterAdditionalModels);
        bus.addListener(this::onModifyBakingResult);
    }

    private void onRegisterAdditionalModels(final ModelEvent.RegisterAdditional event) {
        for (final var woodType : MoreProtectables.WOOD_TYPES) {
            if (woodType == WoodType.WARPED || woodType == WoodType.CRIMSON) {
                continue;
            }
            final var suffix = woodType == WoodType.BAMBOO ? "raft" : "boat";
            final var path = String.format("item/keypad_%s_chest_%s", WoodTypeUtils.getSimpleName(woodType), suffix);
            event.register(new ResourceLocation(MoreProtectables.MODID, path));
        }
    }

    private void onModifyBakingResult(final ModelEvent.ModifyBakingResult event) {
        for (final var woodType : MoreProtectables.WOOD_TYPES) {
            if (woodType == WoodType.WARPED || woodType == WoodType.CRIMSON) {
                continue;
            }
            final var woodName = WoodTypeUtils.getSimpleName(woodType);
            final var models = event.getModels();
            final var suffix = woodType == WoodType.BAMBOO ? "raft" : "boat";
            final var model = models.get(new ResourceLocation(MoreProtectables.MODID,
                String.format("item/keypad_%s_chest_%s", woodName, suffix)));
            models.put(new ModelResourceLocation(SecurityCraft.MODID,
                String.format("%s_security_sea_%s", woodName, suffix),
                "inventory"), model);
        }
    }
}
