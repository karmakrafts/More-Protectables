package io.karma.moreprotectables.util;

import io.karma.moreprotectables.MoreProtectables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public enum MPSounds implements Supplier<SoundEvent> {
    // @formatter:off
    BEEP        ("keypad_beep"),
    BEEP_CONFIRM("keypad_beep_confirm");
    // @formatter:on

    private final SoundEvent event;

    MPSounds(final String name) {
        event = SoundEvent.createVariableRangeEvent(new ResourceLocation(MoreProtectables.MODID, name));
    }

    @Override
    public SoundEvent get() {
        return event;
    }
}
