package io.karma.moreprotectables.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.moreprotectables.client.event.BlockEntityRenderEvent;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
@Mixin(BlockEntityRenderDispatcher.class)
public final class BlockEntityRenderDispatcherMixin {
    @Inject(method = "setupAndRender", at = @At("TAIL"))
    private static <T extends BlockEntity> void onSetupAndRender(final BlockEntityRenderer<T> renderer,
                                                                 final T blockEntity,
                                                                 final float partialTick,
                                                                 final PoseStack poseStack,
                                                                 final MultiBufferSource bufferSource,
                                                                 final CallbackInfo cbi) {
        // @formatter:off
        final var packedLight = blockEntity.hasLevel()
            ? LevelRenderer.getLightColor(Objects.requireNonNull(blockEntity.getLevel()), blockEntity.getBlockPos())
            : 0;
        // @formatter:on
        MinecraftForge.EVENT_BUS.post(new BlockEntityRenderEvent(blockEntity, poseStack, bufferSource, packedLight));
    }
}
