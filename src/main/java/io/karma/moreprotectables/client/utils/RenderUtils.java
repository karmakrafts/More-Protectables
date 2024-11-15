package io.karma.moreprotectables.client.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

/**
 * @author Alexander Hinze
 * @since 28/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class RenderUtils {
    private static int previousStencilFunc;
    private static int previousStencilRef;
    private static int previousStencilMask;
    private static int previousStencilSFail;
    private static int previousStencilDpFail;
    private static int previousStencilDpPass;

    // @formatter:off
    private RenderUtils() {}
    // @formatter:on

    public static void enableOverlayStencil() {
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Make sure we have a fresh stencil each frame
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        setStencilFunc(GL11.GL_EQUAL, 0, 0xFF); // Only draw where stencil is 0
        setStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_INCR); // Increment stencil mask everywhere we render this pass
    }

    public static void disableOverlayStencil() {
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        resetStencilFunc();
        resetStencilOp();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    public static void setStencilFunc(final int func, final int ref, final int mask) {
        previousStencilFunc = GL11.glGetInteger(GL11.GL_STENCIL_FUNC);
        previousStencilRef = GL11.glGetInteger(GL11.GL_STENCIL_REF);
        previousStencilMask = GL11.glGetInteger(GL11.GL_STENCIL_VALUE_MASK);
        GL11.glStencilFunc(func, ref, mask);
    }

    public static void resetStencilFunc() {
        GL11.glStencilFunc(previousStencilFunc, previousStencilRef, previousStencilMask);
    }

    public static void setStencilOp(final int sFail, final int dpFail, final int dpPass) {
        previousStencilSFail = GL11.glGetInteger(GL11.GL_STENCIL_FAIL);
        previousStencilDpFail = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_FAIL);
        previousStencilDpPass = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_PASS);
        GL11.glStencilOp(sFail, dpFail, dpPass);
    }

    public static void resetStencilOp() {
        GL11.glStencilOp(previousStencilSFail, previousStencilDpFail, previousStencilDpPass);
    }

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

    // Cheers Blu =P
    public static void drawBox(final VertexConsumer wr,
                               final PoseStack m,
                               final float x0,
                               final float y0,
                               final float z0,
                               final float x1,
                               final float y1,
                               final float z1,
                               final float r,
                               final float g,
                               final float b,
                               final float a) {
        final var transform = m.last().pose();
        wr.vertex(transform, x0, y0, z1).color(r, g, b, a).endVertex();
        wr.vertex(transform, x1, y0, z1).color(r, g, b, a).endVertex();
        wr.vertex(transform, x1, y1, z1).color(r, g, b, a).endVertex();
        wr.vertex(transform, x0, y1, z1).color(r, g, b, a).endVertex();

        wr.vertex(transform, x0, y1, z0).color(r, g, b, a).endVertex();
        wr.vertex(transform, x1, y1, z0).color(r, g, b, a).endVertex();
        wr.vertex(transform, x1, y0, z0).color(r, g, b, a).endVertex();
        wr.vertex(transform, x0, y0, z0).color(r, g, b, a).endVertex();

        wr.vertex(transform, x0, y0, z0).color(r, g, b, a).endVertex();
        wr.vertex(transform, x1, y0, z0).color(r, g, b, a).endVertex();
        wr.vertex(transform, x1, y0, z1).color(r, g, b, a).endVertex();
        wr.vertex(transform, x0, y0, z1).color(r, g, b, a).endVertex();

        wr.vertex(transform, x0, y1, z1).color(r, g, b, a).endVertex();
        wr.vertex(transform, x1, y1, z1).color(r, g, b, a).endVertex();
        wr.vertex(transform, x1, y1, z0).color(r, g, b, a).endVertex();
        wr.vertex(transform, x0, y1, z0).color(r, g, b, a).endVertex();

        wr.vertex(transform, x0, y0, z0).color(r, g, b, a).endVertex();
        wr.vertex(transform, x0, y0, z1).color(r, g, b, a).endVertex();
        wr.vertex(transform, x0, y1, z1).color(r, g, b, a).endVertex();
        wr.vertex(transform, x0, y1, z0).color(r, g, b, a).endVertex();

        wr.vertex(transform, x1, y1, z0).color(r, g, b, a).endVertex();
        wr.vertex(transform, x1, y1, z1).color(r, g, b, a).endVertex();
        wr.vertex(transform, x1, y0, z1).color(r, g, b, a).endVertex();
        wr.vertex(transform, x1, y0, z0).color(r, g, b, a).endVertex();
    }
}
