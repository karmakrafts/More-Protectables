package io.karma.moreprotectables.compat;

import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Stream;

/**
 * @author Alexander Hinze
 * @since 14/10/2024
 */
public interface CompatibilityModule {
    static Stream<Provider<CompatibilityModule>> loadAll() {
        return ServiceLoader.load(CompatibilityModule.class).stream();
    }

    static String getModId(final Class<? extends CompatibilityModule> type) {
        if (!type.isAnnotationPresent(ModId.class)) {
            throw new IllegalStateException("Mod ID must be specified using @ModId");
        }
        return type.getAnnotation(ModId.class).value();
    }

    default String getModId() {
        return getModId(getClass());
    }

    default String getName() {
        return getModId();
    }

    default void addItemsToTab(final Output output) {
    }

    void init();

    @OnlyIn(Dist.CLIENT)
    default void initClient() {
    }

    default boolean isEnabled() {
        return true;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface ModId {
        String value();
    }
}
