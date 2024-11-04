package io.karma.moreprotectables.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 04/11/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DelegatingBufferSource implements MultiBufferSource {
    private final VertexConsumer consumer;

    public DelegatingBufferSource(final VertexConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public @NotNull VertexConsumer getBuffer(final @NotNull RenderType renderType) {
        return consumer;
    }
}
