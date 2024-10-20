package io.karma.moreprotectables.client.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import io.karma.moreprotectables.client.render.KeypadRenderer;
import net.geforcemods.securitycraft.renderers.SecuritySeaBoatRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Boat.Type;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

/**
 * @author Alexander Hinze
 * @since 20/10/2024
 */
@Mixin(SecuritySeaBoatRenderer.class)
public abstract class SecuritySeaBoatRendererMixin extends BoatRenderer {
    public SecuritySeaBoatRendererMixin(final Context context, final boolean isChestBoat) {
        super(context, isChestBoat);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(final Context context, final CallbackInfo cbi) {
        boatResources = Stream.of(Type.values()).collect(ImmutableMap.toImmutableMap((type) -> type,
            (type) -> Pair.of(new ResourceLocation("minecraft",
                "textures/entity/chest_boat/" + type.getName() + ".png"), createBoatModel(context, type, true))));
    }

    @Override
    public void render(final @NotNull Boat entity,
                       final float yaw,
                       final float partialTicks,
                       final @NotNull PoseStack poseStack,
                       final @NotNull MultiBufferSource bufferSource,
                       final int packedLight) {
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
        poseStack.pushPose();
        // --- BEGIN VANILLA CODE
        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        final var f = (float) entity.getHurtTime() - partialTicks;
        var f1 = entity.getDamage() - partialTicks;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }
        if (f > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float) entity.getHurtDir()));
        }
        final var f2 = entity.getBubbleAngle(partialTicks);
        if (!Mth.equal(f2, 0.0F)) {
            poseStack.mulPose((new Quaternionf()).setAngleAxis(entity.getBubbleAngle(partialTicks) * 0.017453292F,
                1F,
                0F,
                1F));
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        // --- END VANILLA CODE

        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        final var type = Boat.Type.values()[entity.getEntityData().get(Boat.DATA_ID_TYPE)];
        final var offset = type == Type.BAMBOO ? 5F / 16F : 0F;
        poseStack.translate(-(17F / 16F), offset, 1F / 16F);
        final var buffer = bufferSource.getBuffer(RenderType.cutout());
        KeypadRenderer.INSTANCE.renderKeypad(buffer, poseStack, packedLight, OverlayTexture.NO_OVERLAY, true);

        poseStack.popPose();
    }
}
