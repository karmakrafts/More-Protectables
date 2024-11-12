package io.karma.moreprotectables.block;

import com.mojang.blaze3d.vertex.PoseStack;
import io.karma.moreprotectables.blockentity.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.TransformationHelper;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public interface KeypadDoorBlock extends KeypadBlock, IDisguisable, IOverlayDisplay {
    @OnlyIn(Dist.CLIENT)
    default float getKeypadRotation(final BlockState state) {
        if (!state.hasProperty(DoorBlock.OPEN) || !state.hasProperty(DoorBlock.HINGE)) {
            return 180F;
        }
        if (state.getValue(DoorBlock.OPEN)) {
            if (state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT) {
                return 90F;
            }
            return -90F;
        }
        return 180F;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    default void applyKeypadTransform(final PoseStack poseStack,
                                      final BlockState state,
                                      final boolean isItem,
                                      final float angle) {
        poseStack.translate(0.5F, 0.5F, 0.5F);
        final var rotation = getKeypadRotation(state);
        final var actualRotation = isItem ? rotation + 180F : angle + rotation;
        poseStack.mulPose(TransformationHelper.quatFromXYZ(0F, actualRotation, 0F, true));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        final var hinge = state.getValue(DoorBlock.HINGE);
        // Handle open transforms
        if (state.hasProperty(DoorBlock.OPEN) && state.getValue(DoorBlock.OPEN)) {
            if (hinge == DoorHingeSide.LEFT) {
                poseStack.translate(-(10F / 16F), 0F, 12F / 16F);
                return;
            }
            poseStack.translate(-(2F / 16F), 0F, 12F / 16F);
            return;
        }

        // Handle closed transforms
        if (hinge == DoorHingeSide.LEFT) {
            poseStack.translate(-(10F / 16F), 0F, -(1F / 16F));
            return;
        }
        poseStack.translate(-(2F / 16F), 0F, -(1F / 16F));
    }

    @Override
    default DoorBlock getThisBlock() {
        return (DoorBlock) this;
    }

    default InteractionResult useDoor(final BlockState state,
                                      final Level level,
                                      final BlockPos pos,
                                      final Player player,
                                      final InteractionHand hand,
                                      final BlockHitResult hit) {
        if (!level.isClientSide) {
            final var blockEntity = (KeypadDoorBlockEntity) level.getBlockEntity(pos);
            if (blockEntity.isDisabled()) {
                player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
            }
            else if (blockEntity.verifyPasscodeSet(level, pos, blockEntity, player)) {
                if (blockEntity.isDenied(player)) {
                    if (blockEntity.sendsDenylistMessage()) {
                        PlayerUtils.sendMessageToPlayer(player,
                            Utils.localize(getDescriptionId()),
                            Utils.localize("messages.securitycraft:module.onDenylist"),
                            ChatFormatting.RED);
                    }
                }
                else if (blockEntity.isAllowed(player)) {
                    if (blockEntity.sendsAllowlistMessage()) {
                        PlayerUtils.sendMessageToPlayer(player,
                            Utils.localize(getDescriptionId()),
                            Utils.localize("messages.securitycraft:module.onAllowlist"),
                            ChatFormatting.GREEN);
                    }
                    activate(state, level, pos, player, blockEntity.getSignalLength());
                }
                else if (!player.getItemInHand(hand).is(SCContent.CODEBREAKER.get())) {
                    blockEntity.openPasscodeGUI(level, pos, player);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    default void activate(final BlockState state,
                          final Level level,
                          final BlockPos pos,
                          final Player player,
                          final int signalLength) {
        final var open = !(Boolean) state.getValue(DoorBlock.OPEN);
        final var type = getThisBlock().type();
        level.playSound(player,
            pos,
            open ? type.doorOpen() : type.doorClose(),
            SoundSource.BLOCKS,
            1.0F,
            level.getRandom().nextFloat() * 0.1F + 0.9F);
        level.setBlockAndUpdate(pos, state.setValue(DoorBlock.OPEN, open));
        level.updateNeighborsAt(pos, getThisBlock());
        level.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        if (open && signalLength > 0) {
            level.scheduleTick(pos, getThisBlock(), signalLength);
        }
    }
}
