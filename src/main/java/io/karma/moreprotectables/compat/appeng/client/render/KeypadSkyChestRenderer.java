package io.karma.moreprotectables.compat.appeng.client.render;

import appeng.block.storage.SkyChestBlock.SkyChestType;
import appeng.blockentity.storage.SkyChestBlockEntity;
import appeng.client.render.tesr.SkyChestTESR;
import io.karma.moreprotectables.compat.appeng.KeypadSkyChestBlock;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
@OnlyIn(Dist.CLIENT)
public final class KeypadSkyChestRenderer extends SkyChestTESR {
    public KeypadSkyChestRenderer(final Context context) {
        super(context);
    }

    @Override
    protected Material getRenderMaterial(final SkyChestBlockEntity blockEntity) {
        final var block = blockEntity.getBlockState().getBlock();
        if (!(block instanceof KeypadSkyChestBlock chestBlock)) {
            return super.getRenderMaterial(blockEntity);
        }
        if (chestBlock.getType() == SkyChestType.STONE) {
            return TEXTURE_STONE;
        }
        return TEXTURE_BLOCK;
    }
}
