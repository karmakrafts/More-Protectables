package io.karma.moreprotectables.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.moreprotectables.blockentity.SimpleKeypadBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * A dummy block entity renderer to allow keypads to be rendered on static blocks
 * like doors and trapdoors.
 *
 * @author Alexander Hinze
 * @since 19/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DummyBlockEntityRenderer implements BlockEntityRenderer<SimpleKeypadBlockEntity> {
    public DummyBlockEntityRenderer(final Context context) {
    }

    @Override
    public void render(final @NotNull SimpleKeypadBlockEntity blockEntity,
                       final float partialTicks,
                       final @NotNull PoseStack poseStack,
                       final @NotNull MultiBufferSource bufferSource,
                       final int packedLight,
                       final int packedOverlay) {
    }
}
