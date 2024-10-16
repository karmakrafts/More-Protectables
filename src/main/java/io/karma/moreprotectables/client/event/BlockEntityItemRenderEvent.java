package io.karma.moreprotectables.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class BlockEntityItemRenderEvent extends Event {
    private final ItemStack stack;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int packedLight;
    private final int packedOverlay;

    public BlockEntityItemRenderEvent(final ItemStack stack,
                                      final PoseStack poseStack,
                                      final MultiBufferSource bufferSource,
                                      final int packedLight,
                                      final int packedOverlay) {
        this.stack = stack;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
    }

    public ItemStack getStack() {
        return stack;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public MultiBufferSource getBufferSource() {
        return bufferSource;
    }

    public int getPackedLight() {
        return packedLight;
    }

    public int getPackedOverlay() {
        return packedOverlay;
    }
}
