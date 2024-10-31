package io.karma.moreprotectables.client.model;

import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.block.KeypadTrapdoorBlock;
import io.karma.moreprotectables.block.ReinforcedBlockGenerator;
import io.karma.moreprotectables.util.WoodTypeUtils;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 20/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ModelPatcher {
    public static final ModelPatcher INSTANCE = new ModelPatcher();
    private static final ResourceLocation TRAPDOOR_ITEM = new ResourceLocation(MoreProtectables.MODID,
        "item/keypad_trapdoor");
    private static final ResourceLocation REINFORCED_BLOCK_ITEM = new ResourceLocation(MoreProtectables.MODID,
        "item/reinforced_block");
    private final HashMap<ResourceLocation, BakedModel> replacedModels = new HashMap<>();
    private final ArrayList<ResourceLocation> suppressions = new ArrayList<>();

    // @formatter:off
    private ModelPatcher() {}
    // @formatter:on

    @Internal
    public void setup() {
        final var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onRegisterAdditionalModels);
        bus.addListener(this::onModifyBakingResult);
    }

    @Internal
    public void suppressBlockErrors(final Block block) {
        final var name = ForgeRegistries.BLOCKS.getKey(block);
        if (suppressions.contains(name)) {
            return;
        }
        suppressions.add(name);
    }

    // Used from ModelBakeryMixin
    @Internal
    public boolean maySkipLoad(final ResourceLocation location) {
        for (final var suppression : suppressions) {
            if (!location.getNamespace().equals(suppression.getNamespace()) || !location.getPath().contains(suppression.getPath())) {
                continue;
            }
            return true;
        }
        return false;
    }

    private void onRegisterAdditionalModels(final ModelEvent.RegisterAdditional event) {
        for (final var woodType : MoreProtectables.WOOD_TYPES) {
            if (woodType == WoodType.WARPED || woodType == WoodType.CRIMSON) {
                continue;
            }
            final var suffix = woodType == WoodType.BAMBOO ? "raft" : "boat";
            event.register(new ResourceLocation(MoreProtectables.MODID,
                String.format("item/keypad_%s_chest_%s", WoodTypeUtils.getSimpleName(woodType), suffix)));
        }
        event.register(TRAPDOOR_ITEM);
        event.register(REINFORCED_BLOCK_ITEM);
    }

    private void patchSecurityBoatModels(final Map<ResourceLocation, BakedModel> models) {
        MoreProtectables.LOGGER.info("Patching security sea boat models");
        for (final var woodType : MoreProtectables.WOOD_TYPES) {
            if (woodType == WoodType.WARPED || woodType == WoodType.CRIMSON) {
                continue;
            }
            final var woodName = WoodTypeUtils.getSimpleName(woodType);
            final var suffix = woodType == WoodType.BAMBOO ? "raft" : "boat";
            final var model = models.get(new ResourceLocation(MoreProtectables.MODID,
                String.format("item/keypad_%s_chest_%s", woodName, suffix)));
            models.put(new ModelResourceLocation(SecurityCraft.MODID,
                String.format("%s_security_sea_%s", woodName, suffix),
                "inventory"), model);
        }
    }

    private void patchTrapdoorModels(final Map<ResourceLocation, BakedModel> models) {
        MoreProtectables.LOGGER.info("Patching passcode-protected trapdoor models");
        final var trapdoorModel = models.get(TRAPDOOR_ITEM);
        for (final var entry : ForgeRegistries.BLOCKS.getEntries()) {
            if (!(entry.getValue() instanceof KeypadTrapdoorBlock)) {
                continue;
            }
            final var name = entry.getKey().location();
            final var location = new ModelResourceLocation(name, "inventory");
            final var oldModel = models.put(location, trapdoorModel);
            if (oldModel == null) {
                continue;
            }
            replacedModels.put(location, oldModel); // Back up old models so we can use them in the delegate renderer
        }
    }

    private void patchShadowBlockModels(final Map<ResourceLocation, BakedModel> models) {
        MoreProtectables.LOGGER.info("Patching reinforced shadow blockk models");
        final var itemModel = models.get(REINFORCED_BLOCK_ITEM);
        for (final var entry : ReinforcedBlockGenerator.INSTANCE.getGeneratedBlocks().entrySet()) {
            final var shadowBlockName = entry.getKey();
            // Patch up item models to use built-in renderer
            models.put(new ModelResourceLocation(shadowBlockName, "inventory"), itemModel);
            // Patch up block models by copying a ref to the original model for each possible state
            final var shadowBlock = entry.getValue();
            final var shadowedBlock = shadowBlock.getVanillaBlock();
            final var shadowedBlockName = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(shadowedBlock));
            final var states = shadowBlock.stateDefinition.getPossibleStates();
            for (final var state : states) {
                final var variant = BlockModelShaper.statePropertiesToString(state.getValues());
                final var location = new ModelResourceLocation(shadowBlockName, variant);
                models.put(location, models.get(new ModelResourceLocation(shadowedBlockName, variant)));
            }
        }
    }

    private void onModifyBakingResult(final ModelEvent.ModifyBakingResult event) {
        final var models = event.getModels();
        patchSecurityBoatModels(models);
        patchTrapdoorModels(models);
        patchShadowBlockModels(models);
    }

    public @Nullable BakedModel getReplacedModel(final ResourceLocation location) {
        return replacedModels.get(location);
    }
}
