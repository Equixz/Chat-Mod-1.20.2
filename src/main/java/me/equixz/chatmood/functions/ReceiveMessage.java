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
                    configData.lastBombType = parts[0].trim();
                    configData.lastBombWorld = parts[1].trim();
                    configData.save();
                }
            }
            if (!configData.legalToggle) {
                sendLastBombbell();
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
