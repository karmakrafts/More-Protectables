package io.karma.moreprotectables.level;

import io.karma.moreprotectables.hook.LevelHooks;
import io.karma.moreprotectables.hook.NeighborUpdaterHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Alexander Hinze
 * @since 01/11/2024
 */
public final class ShadowLevelWrapper extends Level implements LevelHooks {
    private final Level delegate;

    public ShadowLevelWrapper(final Level delegate) {
        super(delegate.levelData,
            delegate.dimension(),
            delegate.registryAccess(),
            delegate.dimensionTypeRegistration(),
            delegate.getProfilerSupplier(),
            delegate.isClientSide,
            delegate.isDebug(),
            delegate.getBiomeManager().biomeZoomSeed,
            ((NeighborUpdaterHooks) delegate.neighborUpdater).moreprotectables$getMaxChainedNeighborUpdates());
        this.delegate = delegate;
    }

    @Override
    public ShadowLevelWrapper moreprotectables$getShadowLevel() {
        return this;
    }

    @Override
    public void sendBlockUpdated(final @NotNull BlockPos pos,
                                 final @NotNull BlockState oldState,
                                 final @NotNull BlockState newState,
                                 final int flags) {
        delegate.sendBlockUpdated(pos, oldState, newState, flags);
    }

    @Override
    public void playSeededSound(final @Nullable Player player,
                                final double x,
                                final double y,
                                final double z,
                                final @NotNull Holder<SoundEvent> event,
                                final @NotNull SoundSource source,
                                final float volume,
                                final float pitch,
                                final long seed) {
        delegate.playSeededSound(player, x, y, z, event, source, volume, pitch, seed);
    }

    @Override
    public void playSeededSound(final @Nullable Player player,
                                final @NotNull Entity entity,
                                final @NotNull Holder<SoundEvent> event,
                                final @NotNull SoundSource source,
                                final float volume,
                                final float pitch,
                                final long seed) {
        delegate.playSeededSound(player, entity, event, source, volume, pitch, seed);
    }


    @Override
    public boolean setBlock(final @NotNull BlockPos pos,
                            final @NotNull BlockState state,
                            final int flags,
                            final int recursionLeft) {
        return delegate.setBlock(pos, state, flags, recursionLeft);
    }

    @Override
    public @NotNull String gatherChunkSourceStats() {
        return delegate.gatherChunkSourceStats();
    }

    @Override
    public @Nullable Entity getEntity(final int id) {
        return delegate.getEntity(id);
    }

    @Nullable
    @Override
    public MapItemSavedData getMapData(final @NotNull String s) {
        return delegate.getMapData(s);
    }

    @Override
    public void setMapData(final @NotNull String s, final @NotNull MapItemSavedData data) {
        delegate.setMapData(s, data);
    }

    @Override
    public int getFreeMapId() {
        return delegate.getFreeMapId();
    }

    @Override
    public void destroyBlockProgress(final int progress, final @NotNull BlockPos pos, final int flags) {
        delegate.destroyBlockProgress(progress, pos, flags);
    }

    @Override
    public @NotNull Scoreboard getScoreboard() {
        return delegate.getScoreboard();
    }

    @Override
    public @NotNull RecipeManager getRecipeManager() {
        return delegate.getRecipeManager();
    }

    @Override
    public @NotNull LevelEntityGetter<Entity> getEntities() {
        return delegate.getEntities();
    }

    @Override
    public @NotNull LevelTickAccess<Block> getBlockTicks() {
        return delegate.getBlockTicks();
    }

    @Override
    public @NotNull LevelTickAccess<Fluid> getFluidTicks() {
        return delegate.getFluidTicks();
    }

    @Override
    public @NotNull ChunkSource getChunkSource() {
        return delegate.getChunkSource();
    }

    @Override
    public void levelEvent(final @Nullable Player player, final int type, final @NotNull BlockPos pos, final int data) {
        delegate.levelEvent(player, type, pos, data);
    }

    @Override
    public void gameEvent(final @NotNull GameEvent event, final @NotNull Vec3 pos, final @NotNull Context context) {
        delegate.gameEvent(event, pos, context);
    }

    @Override
    public float getShade(final @NotNull Direction direction, final boolean b) {
        return delegate.getShade(direction, b);
    }

    @Override
    public @NotNull List<? extends Player> players() {
        return delegate.players();
    }

    @Override
    public @NotNull Holder<Biome> getUncachedNoiseBiome(final int x, final int y, final int z) {
        return delegate.getUncachedNoiseBiome(x, y, z);
    }

    @Override
    public @NotNull FeatureFlagSet enabledFeatures() {
        return delegate.enabledFeatures();
    }

    @Override
    public void addParticle(final @NotNull ParticleOptions pParticleData,
                            final double pX,
                            final double pY,
                            final double pZ,
                            final double pXSpeed,
                            final double pYSpeed,
                            final double pZSpeed) {
        delegate.addParticle(pParticleData, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
    }

    @Override
    public void addParticle(final @NotNull ParticleOptions pParticleData,
                            final boolean pForceAlwaysRender,
                            final double pX,
                            final double pY,
                            final double pZ,
                            final double pXSpeed,
                            final double pYSpeed,
                            final double pZSpeed) {
        delegate.addParticle(pParticleData, pForceAlwaysRender, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
    }

    @Override
    public void addAlwaysVisibleParticle(final @NotNull ParticleOptions pParticleData,
                                         final double pX,
                                         final double pY,
                                         final double pZ,
                                         final double pXSpeed,
                                         final double pYSpeed,
                                         final double pZSpeed) {
        delegate.addAlwaysVisibleParticle(pParticleData, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
    }

    @Override
    public void addAlwaysVisibleParticle(final @NotNull ParticleOptions pParticleData,
                                         final boolean pIgnoreRange,
                                         final double pX,
                                         final double pY,
                                         final double pZ,
                                         final double pXSpeed,
                                         final double pYSpeed,
                                         final double pZSpeed) {
        delegate.addAlwaysVisibleParticle(pParticleData, pIgnoreRange, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
    }
}
