package me.equixz.chatmod.functions.message;

import me.equixz.chatmod.ChatMod;
import me.equixz.chatmod.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.Objects;

import static me.equixz.chatmod.functions.message.sendChatMessage.sendMessage;

public class lastBombbell {
    public static void sendLastBombbell() {
        Config.ConfigData configData = Config.getConfigData();
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        String prefixBombbellToUse = configData.prefixBombbellToUse;
        String bombBellPrefix = configData.bombBellPrefix;
        String bombType = configData.lastBombType;
        String wcNumber = configData.lastBombWorld;
        if (prefixBombbellToUse == null) {
            player.sendMessage(Text.literal("Please send your log file to a developer. Error #2452").formatted(Formatting.RED), false);
            ChatMod.LOGGER.error("prefixBombbellToUse is empty. Value: {}", prefixBombbellToUse);
            configData.prefixBombbellToUse = "/g ";
            return;
        }
        if (bombBellPrefix == null) {
            player.sendMessage(Text.literal("Please send your log file to a developer. Error #2453").formatted(Formatting.RED), false);
            ChatMod.LOGGER.error("bombBellPrefix is empty. Value: {}", bombBellPrefix);
            configData.bombBellPrefix = "|";
            return;
        }
        if (!bombType.isEmpty()) {
            switch (bombType) {
                case "Combat XP Bomb":
                    if (configData.combatXpBombEnabled) {
                        sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                    }
                    break;
                case "Profession XP Bomb":
                    if (configData.professionXpBombEnabled) {
                        sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                    }
                    break;
                case "Profession Speed Bomb":
                    if (configData.professionSpeedBombEnabled) {
                        sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                    }
                    break;
                case "Dungeon Bomb":
                    if (configData.dungeonBombEnabled) {
                        sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                    }
                    break;
                case "Loot Bomb":
                    if (configData.lootBombEnabled) {
                        sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                    }
                    break;
            }
        }
    }
}
