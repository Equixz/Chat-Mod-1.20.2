package me.equixz.chatmod.functions.message;

import me.equixz.chatmod.config.Config;

import static me.equixz.chatmod.functions.message.sendChatMessage.sendMessage;

public class latestBombbell {
    public static void switchToLatestBombbell() {
        Config.ConfigData configData = Config.getConfigData();
        String wcNumber = configData.lastBombWorld;
        if (wcNumber != null) {
            sendMessage("/switch ", wcNumber);
        }
    }
}
