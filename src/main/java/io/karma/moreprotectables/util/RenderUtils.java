package io.karma.moreprotectables.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 28/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class RenderUtils {
    // @formatter:off
    private RenderUtils() {}
    // @formatter:on

    public static void drawQuad(final PoseStack poseStack,
                                final VertexConsumer consumer,
                                final float x,
                                final float y,
                                final float w,
                                final float h,
                                final float z) {
        final var transformMatrix = poseStack.last().pose();
        consumer.vertex(transformMatrix, x, y, z).uv(0F, 1F).endVertex();
        consumer.vertex(transformMatrix, x + w, y, z).uv(1F, 1F).endVertex();
        consumer.vertex(transformMatrix, x + w, y + h, z).uv(1F, 0F).endVertex();
        consumer.vertex(transformMatrix, x, y + h, z).uv(0F, 0F).endVertex();
    }
}
