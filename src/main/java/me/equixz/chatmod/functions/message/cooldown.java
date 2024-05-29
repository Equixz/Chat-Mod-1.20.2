package me.equixz.chatmod.functions.message;

import me.equixz.chatmod.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class cooldown {
    public static void changeCooldown(String newCooldown) {
        int cooldown = Integer.parseInt(newCooldown);
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        if (cooldown < 1250) {
            player.sendMessage(Text.literal("Cooldown value must be 1250 or higher.").formatted(Formatting.RED), false);
            return;
        }
        Config.ConfigData configData = Config.getConfigData();
        configData.initialDelay = cooldown;
        configData.save();
        if (newCooldown.isEmpty()) {
            player.sendMessage(Text.literal("Please provide a non-empty message!").formatted(Formatting.RED), false);
        } else {
            player.sendMessage(Text.literal("Message cooldown changed to: " + newCooldown).formatted(Formatting.GREEN), false);
        }
    }
}
