package me.equixz.chatmood.eventhandler;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import static me.equixz.chatmood.ChatMod.LOGGER;

public class ChatEvent implements ModInitializer {

    @Override
    public void onInitialize() {
        // Register the ClientChatReceivedEvent
        ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            // Called when a chat message is received on the client
            // You can add your logic here
            // For example, print the chat message to the console
            LOGGER.info(String.valueOf(message));
            LOGGER.warn("ALLOW_CHAT: Chat Message: " + message.getString());
            return false;
        });
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            // Called when a chat message is received on the client
            // You can add your logic here
            // For example, print the chat message to the console
            LOGGER.info(String.valueOf(message));
            LOGGER.warn("ALLOW_GAME: Chat Message: " + message.getString());
            return false;
        });
    }
}
