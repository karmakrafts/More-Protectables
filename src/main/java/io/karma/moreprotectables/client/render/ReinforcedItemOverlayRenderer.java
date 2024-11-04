package io.karma.moreprotectables.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import io.karma.moreprotectables.MoreProtectables;
import io.karma.moreprotectables.client.event.RenderItemEvent;
import io.karma.moreprotectables.util.RenderUtils;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.EmptyTextureStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * @author Alexander Hinze
 * @since 31/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ReinforcedItemOverlayRenderer {
    public static final ReinforcedItemOverlayRenderer INSTANCE = new ReinforcedItemOverlayRenderer();
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(MoreProtectables.MODID,
        "textures/gui/shield.png");
    // @formatter:off
    private static final RenderType OVERLAY_RENDER_TYPE = RenderType.create(
        String.format("%s:reinfored_block_overlay", MoreProtectables.MODID),
        DefaultVertexFormat.POSITION_TEX,
        Mode.QUADS,
        128,
        false,
        false,
        CompositeState.builder()
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setShaderState(RenderStateShard.POSITION_TEX_SHADER)
            .setCullState(RenderStateShard.CULL)
            .setTextureState(new EmptyTextureStateShard(
                () -> RenderSystem.setShaderTexture(0, OVERLAY_TEXTURE),
                () -> {}
            ))
            .createCompositeState(false)
    );
    // @formatter:on

    // @formatter:off
    private ReinforcedItemOverlayRenderer() {}
    // @formatter:on

    @Internal
    public void init() {
        MinecraftForge.EVENT_BUS.addListener(this::onRenderItemPost);
    }

    private void onRenderItemPost(final RenderItemEvent.Post event) {
        // Render GUI overlay
        if (Block.byItem(event.getStack().getItem()) instanceof IReinforcedBlock && event.getDisplayContext() == ItemDisplayContext.GUI) {
            final var poseStack = event.getPoseStack();
            poseStack.pushPose();
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            RenderUtils.drawQuad(poseStack,
                event.getBufferSource().getBuffer(OVERLAY_RENDER_TYPE),
                0.7F,
                0.7F,
                0.25F,
                0.25F,
                2F);
            poseStack.popPose();
        }
    }
}
