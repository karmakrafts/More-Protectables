package io.karma.moreprotectables.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
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
 * @since 26/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ReinforcedShadowBlockItemRenderer extends BlockEntityWithoutLevelRenderer {
    private ReinforcedShadowBlockItemRenderer(final BlockEntityRenderDispatcher dispatcher,
                                              final EntityModelSet entityModels) {
        super(dispatcher, entityModels);
    }

    public static IClientItemExtensions extension() {
        return new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                final var game = Minecraft.getInstance();
                return new ReinforcedShadowBlockItemRenderer(game.getBlockEntityRenderDispatcher(),
                    game.getEntityModels());
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
    }
}
