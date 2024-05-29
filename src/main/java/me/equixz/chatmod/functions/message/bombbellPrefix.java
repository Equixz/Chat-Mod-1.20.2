package me.equixz.chatmod.functions.message;

import me.equixz.chatmod.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.Objects;

public class bombbellPrefix {
    public static void changeBombBellPrefix(String newPrefix) {
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        if (newPrefix.length() <= 15) {
            if (newPrefix.isEmpty()) {
                player.sendMessage(Text.literal("Please provide a non-empty message!").formatted(Formatting.RED), false);
                return;
            }
            player.sendMessage(Text.literal("Bomb Bell prefix changed to: " + newPrefix).formatted(Formatting.GREEN), false);
            Config.ConfigData configData = Config.getConfigData();
            configData.bombBellPrefix = newPrefix;
            configData.save();
        } else {
            player.sendMessage(Text.literal("Please provide a prefix that's under 15 characters!").formatted(Formatting.RED), false);
        }
    }
}
