package io.karma.moreprotectables.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.moreprotectables.client.model.ModelPatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 26/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DelegateBlockItemRenderer extends BlockEntityWithoutLevelRenderer {
    private DelegateBlockItemRenderer(final BlockEntityRenderDispatcher dispatcher,
                                      final EntityModelSet entityModelSet) {
        super(dispatcher, entityModelSet);
    }

    public static IClientItemExtensions extension() {
        return new IClientItemExtensions() {
            private final Lazy<BlockEntityWithoutLevelRenderer> renderer = Lazy.of(() -> {
                final var game = Minecraft.getInstance();
                return new DelegateBlockItemRenderer(game.getBlockEntityRenderDispatcher(), game.getEntityModels());
            });

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer.get();
            }
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderByItem(final @NotNull ItemStack stack,
                             final @NotNull ItemDisplayContext displayContext,
                             final @NotNull PoseStack poseStack,
                             final @NotNull MultiBufferSource bufferSource,
                             final int packedLight,
                             final int packedOverlay) {
        final var block = Block.byItem(stack.getItem());
        if (block == Blocks.AIR) {
            return;
        }
        final var name = ForgeRegistries.BLOCKS.getKey(block);
        if (name == null) {
            return;
        }
        final var location = new ModelResourceLocation(name, "inventory");
        final var model = ModelPatcher.INSTANCE.getReplacedModel(location);
        if (model == null) {
            return;
        }
        final var state = block.defaultBlockState();
        final var renderType = ItemBlockRenderTypes.getRenderType(state, false);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(),
            bufferSource.getBuffer(renderType),
            state,
            model,
            1F,
            1F,
            1F,
            packedLight,
            packedOverlay,
            ModelData.EMPTY,
            renderType);
    }
}
