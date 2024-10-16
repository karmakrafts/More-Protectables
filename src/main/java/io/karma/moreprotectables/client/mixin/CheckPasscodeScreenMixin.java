package io.karma.moreprotectables.client.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import io.karma.moreprotectables.client.gui.KeypadButton;
import io.karma.moreprotectables.util.MPSounds;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.screen.CheckPasscodeScreen;
import net.geforcemods.securitycraft.screen.CheckPasscodeScreen.CensoringEditBox;
import net.geforcemods.securitycraft.screen.components.CallbackCheckbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * @author Alexander Hinze
 * @since 16/10/2024
 */
@Mixin(CheckPasscodeScreen.class)
public abstract class CheckPasscodeScreenMixin extends Screen {
    @Shadow
    @Final
    private static Component COOLDOWN_TEXT_1;
    @Shadow
    private int leftPos;
    @Shadow
    private int topPos;
    @Shadow
    private int cooldownText1XPos;
    @Shadow
    private CensoringEditBox keycodeTextbox;
    @Shadow
    private int imageWidth;
    @Shadow
    private int imageHeight;
    @Shadow
    private IPasscodeProtected passcodeProtected;

    protected CheckPasscodeScreenMixin(final Component title) {
        super(title);
    }

    @Shadow
    protected abstract void addNumberToString(int number);

    @Shadow
    protected abstract void toggleChildrenActive(boolean setActive);

    @Shadow
    protected abstract void removeLastCharacter();

    @Shadow
    public abstract void checkCode(String code);

    @Shadow
    protected abstract boolean isValidChar(char c);

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;init()V", shift = Shift.AFTER), cancellable = true)
    private void onInit(final CallbackInfo cbi) {
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;
        cooldownText1XPos = width / 2 - font.width(COOLDOWN_TEXT_1) / 2;
        addRenderableWidget(new CallbackCheckbox(width / 2 - 37,
            height / 2 - 55,
            12,
            12,
            Component.translatable("gui.securitycraft:passcode.showPasscode"),
            false,
            (newState) -> {
                keycodeTextbox.setCensoring(!newState);
            },
            4210752));
        // @formatter:off
        addRenderableWidget(new KeypadButton(width / 2 - 33, height / 2 - 35, 20, 20, Component.literal("1"), (b) -> addNumberToString(1)));
        addRenderableWidget(new KeypadButton(width / 2 - 8, height / 2 - 35, 20, 20, Component.literal("2"), (b) -> addNumberToString(2)));
        addRenderableWidget(new KeypadButton(width / 2 + 17, height / 2 - 35, 20, 20, Component.literal("3"), (b) -> addNumberToString(3)));
        addRenderableWidget(new KeypadButton(width / 2 - 33, height / 2 - 10, 20, 20, Component.literal("4"), (b) -> addNumberToString(4)));
        addRenderableWidget(new KeypadButton(width / 2 - 8, height / 2 - 10, 20, 20, Component.literal("5"), (b) -> addNumberToString(5)));
        addRenderableWidget(new KeypadButton(width / 2 + 17, height / 2 - 10, 20, 20, Component.literal("6"), (b) -> addNumberToString(6)));
        addRenderableWidget(new KeypadButton(width / 2 - 33, height / 2 + 15, 20, 20, Component.literal("7"), (b) -> addNumberToString(7)));
        addRenderableWidget(new KeypadButton(width / 2 - 8, height / 2 + 15, 20, 20, Component.literal("8"), (b) -> addNumberToString(8)));
        addRenderableWidget(new KeypadButton(width / 2 + 17, height / 2 + 15, 20, 20, Component.literal("9"), (b) -> addNumberToString(9)));
        addRenderableWidget(new KeypadButton(width / 2 - 33, height / 2 + 40, 20, 20, Component.literal("←"), (b) -> removeLastCharacter()));
        addRenderableWidget(new KeypadButton(width / 2 - 8, height / 2 + 40, 20, 20, Component.literal("0"), (b) -> addNumberToString(0)));
        addRenderableWidget(new KeypadButton(width / 2 + 17, height / 2 + 40, 20, 20, Component.literal("✔"), (b) -> checkCode(keycodeTextbox.getValue()), MPSounds.BEEP_CONFIRM));
        // @formatter:on
        keycodeTextbox = addRenderableWidget(new CensoringEditBox(font,
            width / 2 - 37,
            height / 2 - 72,
            77,
            12,
            Component.empty()) {
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return active && super.mouseClicked(mouseX, mouseY, button);
            }

            public boolean canConsumeInput() {
                return active && isVisible();
            }
        });
        keycodeTextbox.setMaxLength(Integer.MAX_VALUE);
        keycodeTextbox.setFilter((s) -> s.matches("\\d*\\**"));
        if (passcodeProtected.isOnCooldown()) {
            toggleChildrenActive(false);
        }
        else {
            setInitialFocus(keycodeTextbox);
        }
        cbi.cancel();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, final CallbackInfoReturnable<Boolean> cbi) {
        if (minecraft == null || keycodeTextbox == null) {
            cbi.setReturnValue(false);
            cbi.cancel();
            return;
        }

        final var player = Objects.requireNonNull(minecraft.player);
        final var codeValue = keycodeTextbox.getValue();

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !codeValue.isEmpty()) {
            player.playSound(MPSounds.BEEP.get(), 0.15F, 1.0F);
        }

        if (!super.keyPressed(keyCode, scanCode, modifiers) && !keycodeTextbox.keyPressed(keyCode,
            scanCode,
            modifiers)) {
            if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
                onClose();
            }

            if (!passcodeProtected.isOnCooldown() && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
                player.playSound(MPSounds.BEEP_CONFIRM.get(), 0.15F, 1.0F);
                checkCode(keycodeTextbox.getValue());
            }
        }

        cbi.setReturnValue(true);
        cbi.cancel();
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void onCharTyped(final char typedChar, final int keyCode, final CallbackInfoReturnable<Boolean> cbi) {
        if (minecraft == null || minecraft.player == null) {
            cbi.setReturnValue(false);
            cbi.cancel();
            return;
        }
        if (!passcodeProtected.isOnCooldown() && isValidChar(typedChar)) {
            keycodeTextbox.charTyped(typedChar, keyCode);
            minecraft.player.playSound(MPSounds.BEEP.get(), 0.15F, 1.0F);
        }

        cbi.setReturnValue(true);
        cbi.cancel();
    }
}
