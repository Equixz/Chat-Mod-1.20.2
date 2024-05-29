package me.equixz.chatmod.functions.message;

import me.equixz.chatmod.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class prefix {
    public static void changePrefix(String newPrefix) {
        Config.ConfigData configData = Config.getConfigData();
        configData.prefixToUse = newPrefix;
        configData.save();
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        player.sendMessage(Text.literal("Message chat changed to: " + newPrefix).formatted(Formatting.GREEN), false);
    }
}
