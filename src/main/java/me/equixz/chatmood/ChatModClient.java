package me.equixz.chatmood;

import me.equixz.chatmood.commands.Message;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import static me.equixz.chatmood.functions.MessageFunctions.sendChatMessage;
import static net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK;

public class ChatModClient implements ClientModInitializer {
    public static String messageToSend = "Crazy";
    public static String prefixToUse = "";
    public static String prefixBombbellToUse = "/g ";
    public static int initialDelay = 1500;
    public static int delayIncrement = 1500;

    private static final KeyBinding sendMessageKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("Send Group Messages", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "ChatMod")
    );

    @Override
    public void onInitializeClient() {
        Message.registerBaseCommand();
        END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        if (sendMessageKey.wasPressed()) {
            sendChatMessage();
        }
    }
}
