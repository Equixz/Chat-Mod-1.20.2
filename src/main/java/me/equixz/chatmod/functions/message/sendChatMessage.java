package me.equixz.chatmod.functions.message;

import net.minecraft.client.MinecraftClient;

public class sendChatMessage {
    public static void sendMessage(String prefix, String message) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(prefix + message);
        }
    }
}
