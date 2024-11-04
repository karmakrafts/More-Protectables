package io.karma.moreprotectables.block;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.moreprotectables.blockentity.KeypadTrapdoorBlockEntity;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.TransformationHelper;

/**
 * @author Alexander Hinze
 * @since 25/10/2024
 */
public interface KeypadTrapdoorBlock extends KeypadBlock {
    @OnlyIn(Dist.CLIENT)
    @Override
    default void applyKeypadTransform(final PoseStack poseStack,
                                      final BlockState state,
                                      final boolean isItem,
                                      final float angle) {
        final var facing = state.getValue(HorizontalDirectionalBlock.FACING);
        final var rotationOffset = facing.getAxis() == Axis.Z ? 180F : 0F;
        final var isOpen = state.getValue(TrapDoorBlock.OPEN);
        if (isOpen) {
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(TransformationHelper.quatFromXYZ(0F, 180F + angle, 0F, true));
            poseStack.mulPose(TransformationHelper.quatFromXYZ(0F, 0F, 180F, true));
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            poseStack.translate(-(2F / 16F), 2F / 16F, -(1F / 16F));
        }
        else {
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(TransformationHelper.quatFromXYZ(90F, 0F, 0F, true));
            poseStack.mulPose(TransformationHelper.quatFromXYZ(0F, 0F, 180F + angle + rotationOffset, true));
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            poseStack.translate(-(2F / 16F), 2F / 16F, 12F / 16F);
        }
    }

    // TODO: clean this up..
    default InteractionResult useTrapdoor(final BlockState state,
                                          final Level level,
                                          final BlockPos pos,
                                          final Player player,
                                          final InteractionHand hand,
                                          final BlockHitResult hit) {
        final var be = (KeypadTrapdoorBlockEntity) level.getBlockEntity(pos);
        if (state.getValue(TrapDoorBlock.OPEN)) {
            return InteractionResult.PASS;
        }
        else {
            if (!level.isClientSide) {
                if (be.isDisabled()) {
                    player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
                }
                else if (be.verifyPasscodeSet(level, pos, be, player)) {
                    if (be.isDenied(player)) {
                        if (be.sendsDenylistMessage()) {
                            PlayerUtils.sendMessageToPlayer(player,
                                Utils.localize(getDescriptionId()),
                                Utils.localize("messages.securitycraft:module.onDenylist"),
                                ChatFormatting.RED);
                        }
                    }
                    else if (be.isAllowed(player)) {
                        if (be.sendsAllowlistMessage()) {
                            PlayerUtils.sendMessageToPlayer(player,
                                Utils.localize(getDescriptionId()),
                                Utils.localize("messages.securitycraft:module.onAllowlist"),
                                ChatFormatting.GREEN);
                        }
                        activate(state, level, pos, player, be.getSignalLength());
                    }
                    else if (!player.getItemInHand(hand).is(SCContent.CODEBREAKER.get())) {
                        be.openPasscodeGUI(level, pos, player);
                    }
                }
            }

            return InteractionResult.SUCCESS;
        }
    }

    @Override
    default void activate(final BlockState state,
                          final Level level,
                          final BlockPos pos,
                          final Player player,
                          final int signalLength) {
        level.setBlockAndUpdate(pos, state.cycle(TrapDoorBlock.OPEN));
        ((TrapDoorBlock) state.getBlock()).playSound(null, level, pos, true);
        if (signalLength > 0) {
            level.scheduleTick(pos, getThisBlock(), signalLength);
        }
    }
}
