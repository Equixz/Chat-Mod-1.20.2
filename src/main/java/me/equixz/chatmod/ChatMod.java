package me.equixz.chatmod;

import static me.equixz.chatmod.structure.FileCreation.createFile;
import static me.equixz.chatmod.structure.LoadData.downloadFile;
import static me.equixz.chatmod.structure.FileEmpty.isFileEmpty;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatMod implements ModInitializer {

    @Override
    public void onInitialize() {
        createFile("config/ChatMod/Files/Crazy");
        createFile("config/ChatMod/Files/Uwu");
        String fileName = "config/ChatMod/Files/Crazy.txt";
        String rawLink = "https://pastebin.com/raw/D4qNzZEU";
        if (isFileEmpty(fileName)) {
            LOGGER.info("File {} is not empty. Skipping download.", fileName);
        } else {
            downloadFile(fileName, rawLink);
        }
        fileName = "config/ChatMod/Files/Uwu.txt";
        rawLink = "https://pastebin.com/raw/zrv1X1SH";
        if (isFileEmpty(fileName)) {
            LOGGER.info("File {} is not empty. Skipping download.", fileName);
        } else {
            downloadFile(fileName, rawLink);
        }
    }

    private static final String MOD_ID = "chat-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
}
