package io.karma.moreprotectables.client.mixin;

import io.karma.moreprotectables.client.hook.BlockColorsHooks;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.Holder.Reference;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

/**
 * @author Alexander Hinze
 * @since 31/10/2024
 */
@Mixin(BlockColors.class)
public final class BlockColorsMixin implements BlockColorsHooks {
    @Shadow
    @Final
    private Map<Reference<Block>, BlockColor> blockColors;

    @Override
    public @Nullable BlockColor moreprotectable$getBlockColor(final Block block) {
        return blockColors.get(ForgeRegistries.BLOCKS.getDelegateOrThrow(block));
    }

    @Override
    public @Nullable BlockColor moreprotectable$removeBlockColor(final Block block) {
        return blockColors.remove(ForgeRegistries.BLOCKS.getDelegateOrThrow(block));
    }
}
