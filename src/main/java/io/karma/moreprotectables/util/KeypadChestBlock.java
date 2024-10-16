package io.karma.moreprotectables.util;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public interface KeypadChestBlock extends IDisguisable, IOverlayDisplay {
    void activate(final BlockState state, final Level level, final BlockPos pos, final Player player);

    String getDescriptionId();

    default @NotNull InteractionResult useChest(final @NotNull BlockState state,
                                                final @NotNull Level level,
                                                final @NotNull BlockPos pos,
                                                final @NotNull Player player,
                                                final @NotNull InteractionHand hand,
                                                final @NotNull BlockHitResult hit) {
        if (!level.isClientSide && !net.geforcemods.securitycraft.blocks.KeypadChestBlock.isBlocked(level, pos)) {
            final var blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof KeypadChestBlockEntity chestBlockEntity)) {
                return InteractionResult.PASS;
            }
            if (chestBlockEntity.verifyPasscodeSet(level, pos, chestBlockEntity, player)) {
                if (chestBlockEntity.isDenied(player)) {
                    if (chestBlockEntity.sendsDenylistMessage()) {
                        PlayerUtils.sendMessageToPlayer(player,
                            Utils.localize(getDescriptionId()),
                            Utils.localize("messages.securitycraft:module.onDenylist"),
                            ChatFormatting.RED);
                    }
                }
                else if (chestBlockEntity.isAllowed(player)) {
                    if (chestBlockEntity.sendsAllowlistMessage()) {
                        PlayerUtils.sendMessageToPlayer(player,
                            Utils.localize(getDescriptionId()),
                            Utils.localize("messages.securitycraft:module.onAllowlist"),
                            ChatFormatting.GREEN);
                    }
                    activate(state, level, pos, player);
                }
                else if (!player.getItemInHand(hand).is(SCContent.CODEBREAKER.get())) {
                    chestBlockEntity.openPasscodeGUI(level, pos, player);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }
}
