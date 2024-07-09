package me.equixz.chatmod;

import me.equixz.chatmod.commands.*;
import me.equixz.chatmod.functions.*;
import me.equixz.chatmod.functions.message.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import java.util.Objects;

public class ChatModClient implements ClientModInitializer {
    private static final KeyBinding sendMessageKey = registerKeyBinding("Send Group Messages");
    private static final KeyBinding sendLastBombbell = registerKeyBinding("Send Last Bombbell");
    private static final KeyBinding switchWorldsLastBombbell = registerKeyBinding("Switch Worlds to latest Bombbell");
    private static final KeyBinding nbtData = registerKeyBinding("Get NBT Data");

    @Override
    public void onInitializeClient() {
        Message.registerBaseCommand();
        ClientTickEvents.END_CLIENT_TICK.register(this::handleClientTick);
        handleAfterInit();
    }

    private static void handleAfterInit() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaleWidth, scaleHeight) -> {
            if (screen != null) {
                ScreenKeyboardEvents.afterKeyPress(screen).register((parent, key, scancode, modifiers) -> {
                    if (nbtData.matchesKey(key, scancode)) {
                        Slot slotUnderMouse = getSlotUnderMouse.getSlot();
                        if (slotUnderMouse != null && slotUnderMouse.hasStack()) {
                            ItemStack itemStack = slotUnderMouse.getStack();
                            if (itemStack.getCount() != 1) return;
                            NBTExtractor.getNBTData(itemStack);
                        }
                    }
                });
            }
        });
    }

    private static KeyBinding registerKeyBinding(String description) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(description, InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "ChatMod"));
    }

    private void handleClientTick(MinecraftClient client) {
        if (sendMessageKey.wasPressed()) {
            chatMessage.sendChatMessage();
        }
        if (sendLastBombbell.wasPressed()) {
            lastBombbell.sendLastBombbell();
        }
        if (switchWorldsLastBombbell.wasPressed()) {
            latestBombbell.switchToLatestBombbell();
        }
        if (nbtData.wasPressed()) {
            NBTExtractor.getNBTData(Objects.requireNonNull(client.player).getMainHandStack());
        }
    }
}