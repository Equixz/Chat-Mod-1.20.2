package me.equixz.chatmood.functions;

import me.equixz.chatmood.config.Config;
import net.minecraft.text.Text;

import java.util.Objects;

import static me.equixz.chatmood.ChatModClient.prefixBombbellToUse;
import static me.equixz.chatmood.functions.MessageFunctions.sendMessage;

public class receiveMessage {

    public static String bombBellPrefix = "[Snow Bell]";
    private static receiveMessage instance;

    public void receiveMessages(Text message) {
        if(Config.getConfigData().BombBellState){
            // Convert Text to unformatted string (removing formatting codes)
            String unformattedMessage = message.getString().replaceAll("(?i)§[0-9A-FK-ORXa-fk-orx]", "");

            // Check if the unformatted message starts with "[Bomb Bell]"
            String bombType = null;
            String wcNumber = null;
            if (unformattedMessage.startsWith("[Bomb Bell]")) {
                // Remove the "[Bomb Bell]" substring from the message
                unformattedMessage = unformattedMessage.substring("[Bomb Bell]".length()).trim();

                // Find the index of "thrown a" in the message
                int thrownIndex = unformattedMessage.indexOf("thrown a");
                if (thrownIndex != -1) {
                    // Remove the "thrown a" substring from the message
                    unformattedMessage = unformattedMessage.substring(thrownIndex + "thrown a".length()).trim();
                }
                // Split the message into bomb type and WC number
                String[] parts = unformattedMessage.split("on WC");
                if (parts.length == 2) {
                    bombType = parts[0].trim();
                    wcNumber = parts[1].trim();
                }
                if(Objects.equals(bombType, "Combat XP Bomb") && Config.getConfigData().combatXpBombEnabled){
                    sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                }
                if(Objects.equals(bombType, "Profession XP Bomb") && Config.getConfigData().professionXpBombEnabled){
                    sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                }
                if(Objects.equals(bombType, "Profession Speed Bomb") && Config.getConfigData().professionSpeedBombEnabled){
                    sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                }
                if(Objects.equals(bombType, "Dungeon Bomb") && Config.getConfigData().dungeonBombEnabled){
                    sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                }
                if(Objects.equals(bombType, "Loot Bomb") && Config.getConfigData().lootBombEnabled){
                    sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                }

            }
        }
    }

    public static receiveMessage getInstance() {
        if (instance == null) {
            instance = new receiveMessage();
        }
        return instance;
    }
}
