package me.equixz.chatmood.functions;

import me.equixz.chatmood.config.Config;
import net.minecraft.text.Text;

import static me.equixz.chatmood.functions.MessageFunctions.sendLastBombbell;

public class ReceiveMessage {
    public void receiveMessages(Text message) {
        Config.ConfigData configData = Config.getConfigData();
        if (configData != null && configData.bombBellState) {
            String unformattedMessage = message.getString().replaceAll("(?i)§[0-9A-FK-ORXa-fk-orx]", "");

            if (unformattedMessage.startsWith("[Bomb Bell]")) {
                unformattedMessage = unformattedMessage.substring("[Bomb Bell]".length()).trim();

                int thrownIndex = unformattedMessage.indexOf("thrown a");
                if (thrownIndex != -1) {
                    unformattedMessage = unformattedMessage.substring(thrownIndex + "thrown a".length()).trim();
                }
                String[] parts = unformattedMessage.split("on WC");
                if (parts.length == 2) {
                    if ((parts[0].contains("Combat XP") && configData.combatXpBombEnabled)) {
                        Save(parts);
                    } else if ((parts[0].contains("Profession XP") && configData.professionXpBombEnabled)) {
                        Save(parts);
                    } else if ((parts[0].contains("Profession Speed") && configData.professionSpeedBombEnabled)) {
                        Save(parts);
                    } else if ((parts[0].contains("Dungeon") && configData.dungeonBombEnabled)) {
                        Save(parts);
                    } else if ((parts[0].contains("Loot") && configData.lootBombEnabled)) {
                        Save(parts);
                    }
                }
            }
            if (!configData.legalToggle) {
                sendLastBombbell();
            }
        }
    }

    private void Save(String[] parts) {
        Config.ConfigData configData = Config.getConfigData();
        configData.lastBombType = parts[0].trim();
        configData.lastBombWorld = parts[1].trim();
        configData.save();
    }

    private static ReceiveMessage instance;

    public static ReceiveMessage getReceiveMessageInstance() {
        if (instance == null) {
            instance = new ReceiveMessage();
        }
        return instance;
    }
}
