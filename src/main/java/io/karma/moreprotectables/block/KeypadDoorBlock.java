package io.karma.moreprotectables.block;

import io.karma.moreprotectables.blockentity.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

/**
 * @author Alexander Hinze
 * @since 19/10/2024
 */
public interface KeypadDoorBlock extends KeypadBlock {
    Vector3f DEFAULT_OFFSET = new Vector3f(10F / 16F, 0F, 1F / 16F);
    Vector3f OPEN_OFFSET = new Vector3f(10F / 16F, 0F, -(12F / 16F));
    float DEFAULT_ROTATION_OFFSET = 180F;
    float OPEN_ROTATION_OFFSET = 90F;

    @OnlyIn(Dist.CLIENT)
    @Override
    default float getKeypadRotationOffset(final BlockState state) {
        if (!state.hasProperty(DoorBlock.OPEN) || !state.hasProperty(DoorBlock.FACING)) {
            return DEFAULT_ROTATION_OFFSET;
        }
        if (state.getValue(DoorBlock.OPEN)) {
            final var facing = state.getValue(DoorBlock.FACING);
            if (facing == Direction.EAST || facing == Direction.NORTH) {
                return -OPEN_ROTATION_OFFSET;
            }
            return OPEN_ROTATION_OFFSET;
        }
        return DEFAULT_ROTATION_OFFSET;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    default Vector3f getKeypadOffset(final BlockState state) {
        if (state.hasProperty(DoorBlock.OPEN) && state.getValue(DoorBlock.OPEN)) {
            return OPEN_OFFSET;
        }
        return DEFAULT_OFFSET;
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
                    activate(state, level, pos, player);
                }
                else if (!player.getItemInHand(hand).is(SCContent.CODEBREAKER.get())) {
                    blockEntity.openPasscodeGUI(level, pos, player);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    default void activate(final BlockState state, final Level level, final BlockPos pos, final Player player) {
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
        final var blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof KeypadDoorBlockEntity doorBlockEntity)) {
            return;
        }
        final var signalLength = doorBlockEntity.getSignalLength();
        if (open && signalLength > 0) {
            level.scheduleTick(pos, getThisBlock(), signalLength);
        }
    }
}
