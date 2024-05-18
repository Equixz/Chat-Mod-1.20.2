package me.equixz.chatmood;

import me.equixz.chatmood.structure.FileCreation;
import me.equixz.chatmood.structure.LoadData;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ChatMod implements ModInitializer {

    @Override
    public void onInitialize() {
        try {
            new ModUpdater().checkForUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileCreation.createFile("config/ChatMod/Files/Crazy");
        FileCreation.createFile("config/ChatMod/Files/Uwu");
        String fileName = "config/ChatMod/Files/Crazy.txt";
        String rawLink = "https://pastebin.com/raw/D4qNzZEU";
        if (!isFileEmpty(fileName)) {
            LOGGER.info("File {} is not empty. Skipping download.", fileName);
        } else {
            LoadData.downloadFile(fileName, rawLink);
        }

        fileName = "config/ChatMod/Files/Uwu.txt";
        rawLink = "https://pastebin.com/raw/zrv1X1SH";
        if (!isFileEmpty(fileName)) {
            LOGGER.info("File {} is not empty. Skipping download.", fileName);
        } else {
            LoadData.downloadFile(fileName, rawLink);
        }
    }

    private boolean isFileEmpty(String fileName) {
        String filePath = "config/ChatMod/Files/";
        File file = new File(filePath, fileName);
        if (file.exists()) {
            return file.length() == 0L;
        } else {
            // Handle case where file doesn't exist
            LOGGER.warn("File {} does not exist.", fileName);
            return false;
        }
    }

    private static final String MOD_ID = "chat-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
}
