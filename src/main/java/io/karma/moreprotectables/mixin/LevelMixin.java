package io.karma.moreprotectables.mixin;

import io.karma.moreprotectables.hook.LevelHooks;
import io.karma.moreprotectables.level.ShadowLevelWrapper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Alexander Hinze
 * @since 01/11/2024
 */
@Mixin(Level.class)
public final class LevelMixin implements LevelHooks {
    // @formatter:off
    @Unique
    private final Lazy<ShadowLevelWrapper> moreprotectables$shadowWrapper = Lazy.of(
        () -> new ShadowLevelWrapper(Level.class.cast(this)));
    // @formatter:on

    @Override
    public ShadowLevelWrapper moreprotectables$getShadowLevel() {
        return moreprotectables$shadowWrapper.get();
    }
}
