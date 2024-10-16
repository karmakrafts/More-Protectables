package io.karma.moreprotectables.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class BlockEntityRenderEvent extends Event {
    private final BlockEntity blockEntity;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int packedLight;

    public BlockEntityRenderEvent(final BlockEntity blockEntity,
                                  final PoseStack poseStack,
                                  final MultiBufferSource bufferSource,
                                  final int packedLight) {
        this.blockEntity = blockEntity;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.packedLight = packedLight;
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
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
}
