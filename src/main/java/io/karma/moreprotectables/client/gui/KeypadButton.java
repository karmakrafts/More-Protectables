package io.karma.moreprotectables.client.gui;

import io.karma.moreprotectables.util.MPSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
public final class KeypadButton extends Button {
    private final Supplier<SoundEvent> sound;

    public KeypadButton(final int x,
                        final int y,
                        final int w,
                        final int h,
                        final Component text,
                        final OnPress onPress,
                        final Supplier<SoundEvent> sound) {
        super(x, y, w, h, text, onPress, Button.DEFAULT_NARRATION);
        this.sound = sound;
    }

    public KeypadButton(final int x,
                        final int y,
                        final int w,
                        final int h,
                        final Component message,
                        final OnPress onPress) {
        this(x, y, w, h, message, onPress, MPSounds.BEEP);
    }

    @Override
    public void playDownSound(final @NotNull SoundManager handler) {
        Objects.requireNonNull(Minecraft.getInstance().player).playSound(sound.get(), 0.15F, 1.0F);
    }
}
