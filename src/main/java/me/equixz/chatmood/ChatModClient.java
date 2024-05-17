package me.equixz.chatmood;

import me.equixz.chatmood.commands.Message;
import me.equixz.chatmood.functions.MessageFunctions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class ChatModClient implements ClientModInitializer {
    private static final KeyBinding sendMessageKey = registerKeyBinding("Send Group Messages");
    private static final KeyBinding sendLastBombbell = registerKeyBinding("Send Last Bombbell");
    private static final KeyBinding switchWorldsLastBombbell = registerKeyBinding("Switch Worlds to latest Bombbell");

    @Override
    public void onInitializeClient() {
        Message.registerBaseCommand();
        ClientTickEvents.END_CLIENT_TICK.register(client -> handleClientTick());
    }

    private static KeyBinding registerKeyBinding(String description) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(description, InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "ChatMod"));
    }

    private static void handleClientTick() {
        if (sendMessageKey.wasPressed()) {
            MessageFunctions.sendChatMessage();
        }
        if (sendLastBombbell.wasPressed()) {
            MessageFunctions.sendLastBombbell();
        }
        if (switchWorldsLastBombbell.wasPressed()) {
            MessageFunctions.switchToLatestBombbell();
        }
    }
}
