package io.karma.moreprotectables.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.moreprotectables.client.hooks.ItemRendererHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class BlockEntityItemRenderer extends BlockEntityWithoutLevelRenderer {
    private BlockEntityItemRenderer(final BlockEntityRenderDispatcher dispatcher, final EntityModelSet entityModels) {
        super(dispatcher, entityModels);
    }

    public static IClientItemExtensions extension() {
        return new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                final var game = Minecraft.getInstance();
                return new BlockEntityItemRenderer(game.getBlockEntityRenderDispatcher(), game.getEntityModels());
            }
        };
    }

    @Override
    public void renderByItem(final @NotNull ItemStack stack,
                             final @NotNull ItemDisplayContext displayContext,
                             final @NotNull PoseStack poseStack,
                             final @NotNull MultiBufferSource bufferSource,
                             final int packedLight,
                             final int packedOverlay) {
        final var itemRenderer = Minecraft.getInstance().getItemRenderer();
        final var blockEntity = ((ItemRendererHooks) itemRenderer).moreprotectables$getBlockEntity(stack);
        final var renderer = blockEntityRenderDispatcher.getRenderer(blockEntity);
        if (renderer == null) {
            return;
        }
        renderer.render(blockEntity, 0F, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
