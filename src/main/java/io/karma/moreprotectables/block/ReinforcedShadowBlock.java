package io.karma.moreprotectables.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 26/10/2024
 */
public final class ReinforcedShadowBlock extends Block {
    private final Supplier<Block> shadowedBlock;

    public ReinforcedShadowBlock(final Supplier<Block> shadowedBlock) {
        super(Properties.of());
        this.shadowedBlock = shadowedBlock;
    }

    public Block getShadowedBlock() {
        return shadowedBlock.get();
    }

    // Stubbed out cause we force-set stateDefinition through an AT
    @Override
    public void createBlockStateDefinition(final @NotNull Builder<Block, BlockState> builder) {
    }
}
