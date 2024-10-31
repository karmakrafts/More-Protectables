package io.karma.moreprotectables.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author Alexander Hinze
 * @since 31/10/2024
 */
@OnlyIn(Dist.CLIENT)
public class RenderItemEvent extends Event {
    private final ItemStack stack;
    private final ItemDisplayContext displayContext;
    private final MultiBufferSource bufferSource;
    private final PoseStack poseStack;
    private final boolean isLeftHand;
    private final int packedLight;
    private final int packedOverlay;

    protected RenderItemEvent(final ItemStack stack,
                              final ItemDisplayContext displayContext,
                              final MultiBufferSource bufferSource,
                              final PoseStack poseStack,
                              final boolean isLeftHand,
                              final int packedLight,
                              final int packedOverlay) {
        this.stack = stack;
        this.displayContext = displayContext;
        this.bufferSource = bufferSource;
        this.poseStack = poseStack;
        this.isLeftHand = isLeftHand;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
    }

    public ItemStack getStack() {
        return stack;
    }

    public ItemDisplayContext getDisplayContext() {
        return displayContext;
    }

    public MultiBufferSource getBufferSource() {
        return bufferSource;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public boolean isLeftHand() {
        return isLeftHand;
    }

    public int getPackedLight() {
        return packedLight;
    }

    public int getPackedOverlay() {
        return packedOverlay;
    }

    @Cancelable
    public static final class Pre extends RenderItemEvent {
        public Pre(final ItemStack stack,
                   final ItemDisplayContext displayContext,
                   final MultiBufferSource bufferSource,
                   final PoseStack poseStack,
                   final boolean isLeftHand,
                   final int packedLight,
                   final int packedOverlay) {
            super(stack, displayContext, bufferSource, poseStack, isLeftHand, packedLight, packedOverlay);
        }
    }

    public static final class Post extends RenderItemEvent {
        public Post(final ItemStack stack,
                    final ItemDisplayContext displayContext,
                    final MultiBufferSource bufferSource,
                    final PoseStack poseStack,
                    final boolean isLeftHand,
                    final int packedLight,
                    final int packedOverlay) {
            super(stack, displayContext, bufferSource, poseStack, isLeftHand, packedLight, packedOverlay);
        }
    }
}
