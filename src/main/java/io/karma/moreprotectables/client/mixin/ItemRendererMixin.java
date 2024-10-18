package io.karma.moreprotectables.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.moreprotectables.client.event.BlockEntityRenderEvent;
import io.karma.moreprotectables.client.hooks.ItemRendererHooks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
@Mixin(ItemRenderer.class)
public final class ItemRendererMixin implements ItemRendererHooks {
    @Unique
    private final HashMap<ResourceLocation, BlockEntity> moreprotectables$blockEntityCache = new HashMap<>();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockEntityWithoutLevelRenderer;renderByItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", shift = Shift.AFTER))
    private void onRender(final ItemStack stack,
                          final ItemDisplayContext displayContext,
                          final boolean leftHand,
                          final PoseStack poseStack,
                          final MultiBufferSource buffer,
                          final int combinedLight,
                          final int combinedOverlay,
                          final BakedModel model,
                          final CallbackInfo cbi) {
        final var blockEntity = moreprotectables$getBlockEntity(stack);
        if (blockEntity != null) {
            MinecraftForge.EVENT_BUS.post(new BlockEntityRenderEvent(blockEntity,
                poseStack,
                buffer,
                combinedLight,
                combinedOverlay,
                true));
        }
    }

    @Override
    public @Nullable BlockEntity moreprotectables$getBlockEntity(final ItemStack stack) {
        final var item = stack.getItem();
        final var block = Block.byItem(item);
        if (!(block instanceof EntityBlock entityBlock)) {
            return null;
        }
        return moreprotectables$blockEntityCache.computeIfAbsent(ForgeRegistries.ITEMS.getKey(item),
            i -> entityBlock.newBlockEntity(BlockPos.ZERO, block.defaultBlockState()));
    }
}
