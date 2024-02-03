package me.equixz.chatmood;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import static me.equixz.chatmood.commands.Message.registerCommands;
import static me.equixz.chatmood.commands.Message.registerCustomCommands;
import static me.equixz.chatmood.functions.MessageFunctions.sendChatMessage;
import static net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK;

public class ChatModClient implements ClientModInitializer {

    private static final KeyBinding sendMessageKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("Send Group Messages", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "ChatMod")
    );

    public static String messageToSend = "Crazy";
    public static String prefixToUse = "";
    public static int initialDelay = 1000;
    public static int delayIncrement = 1000;
    private final CommandDispatcher<FabricClientCommandSource> dispatcher;

    public ChatModClient(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void onInitializeClient() {
        registerCommands(dispatcher);
        registerCustomCommands();
        END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        if (sendMessageKey.wasPressed()) {
            sendChatMessage();
        }
    }

}