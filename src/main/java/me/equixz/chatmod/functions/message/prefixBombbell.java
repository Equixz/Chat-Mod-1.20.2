package me.equixz.chatmod.functions.message;

import me.equixz.chatmod.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class prefixBombbell {
    public static void changePrefixBombbell(String newPrefix) {
        Config.ConfigData configData = Config.getConfigData();
        configData.prefixBombbellToUse = newPrefix;
        configData.save();
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        player.sendMessage(Text.literal("Bomb Bell chat changed to: " + newPrefix).formatted(Formatting.GREEN), false);
    }
}
