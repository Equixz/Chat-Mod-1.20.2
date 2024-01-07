package me.equixz.chatmood;

import me.equixz.chatmood.commands.Message;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import static me.equixz.chatmood.functions.MessageFunctions.*;
import static net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.*;

public class ChatModClient implements ClientModInitializer {

    private static final KeyBinding sendMessageKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("Send Group Messages", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "ChatMod")
    );

    public static String messageToSend = "Crazy";
    public static String prefixToUse = "";
    public static int initialDelay = 1000;
    public static int delayIncrement = 1000;

    @Override
    public void onInitializeClient() {
        Message.registerCommands();
        END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        if (sendMessageKey.wasPressed()) {
            sendChatMessage();
        }
    }

}