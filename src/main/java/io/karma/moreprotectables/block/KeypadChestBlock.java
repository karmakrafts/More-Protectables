package io.karma.moreprotectables.block;

import io.karma.moreprotectables.blockentity.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public interface KeypadChestBlock extends KeypadBlock {
    Vector3f DEFAULT_OFFSET = new Vector3f(2F / 16F, 5F / 16F, 0F);

    @OnlyIn(Dist.CLIENT)
    @Override
    default Vector3f getKeypadOffset(final BlockState state) {
        return DEFAULT_OFFSET;
    }

    @SuppressWarnings("deprecation")
    @Override
    default void activate(final BlockState state, final Level level, final BlockPos pos, final Player player) {
        if (!level.isClientSide) {
            final var blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                final var menuProvider = getThisBlock().getMenuProvider(state, level, pos);
                if (menuProvider != null) {
                    player.openMenu(menuProvider);
                    player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
                }
            }
        }
    }

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
