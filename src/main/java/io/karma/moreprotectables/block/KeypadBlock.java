package io.karma.moreprotectables.block;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

/**
 * @author Alexander Hinze
 * @since 18/10/2024
 */
public interface KeypadBlock extends EntityBlock, IOverlayDisplay, IDisguisable {
    Vector3f DEFAULT_OFFSET = new Vector3f(0F);

    @OnlyIn(Dist.CLIENT)
    default Vector3f getKeypadOffset(final BlockState state) {
        return DEFAULT_OFFSET;
    }

    @OnlyIn(Dist.CLIENT)
    default float getKeypadRotationOffset(final BlockState state) {
        return 0F;
    }

    default Block getThisBlock() {
        return (Block) this;
    }

    String getDescriptionId();

    void activate(final BlockState state, final Level level, final BlockPos pos, final Player player);
}
