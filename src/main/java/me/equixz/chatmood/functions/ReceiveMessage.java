package me.equixz.chatmood.functions;

import me.equixz.chatmood.config.Config;
import net.minecraft.text.Text;

public class ReceiveMessage {
    public void receiveMessages(Text message) {
        if (Config.getConfigData() != null && Config.getConfigData().bombBellState) {
            // Convert Text to unformatted string (removing formatting codes)
            String unformattedMessage = message.getString().replaceAll("(?i)§[0-9A-FK-ORXa-fk-orx]", "");

            // Check if the unformatted message starts with "[Bomb Bell]"
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
                    String bombType = parts[0].trim();
                    String wcNumber = parts[1].trim();

                    Config.ConfigData configData = Config.getConfigData();
                    if (configData != null) {
                        configData.lastBombWorld = wcNumber;
                        configData.lastBombType = bombType;
                        configData.save();
                    }
                }
            }
        }
    }

    private static ReceiveMessage instance;

    public static ReceiveMessage getReceiveMessageInstance() {
        if (instance == null) {
            instance = new ReceiveMessage();
        }
        return instance;
    }
}
