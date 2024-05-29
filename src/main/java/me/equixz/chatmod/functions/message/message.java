package me.equixz.chatmod.functions.message;

import me.equixz.chatmod.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class message {
    public static void changeMessage(String newMessage) {
        Config.ConfigData configData = Config.getConfigData();
        configData.messageToSend = newMessage;
        configData.save();
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        if (newMessage.isEmpty()) {
            player.sendMessage(Text.literal("Please provide an existing file name!").formatted(Formatting.RED), false);
        } else {
            player.sendMessage(Text.literal("File output changed to: " + newMessage).formatted(Formatting.GREEN), false);
        }
    }
}
