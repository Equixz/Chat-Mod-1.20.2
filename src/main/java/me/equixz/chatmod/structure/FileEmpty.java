package me.equixz.chatmod.structure;

import java.io.File;

import static me.equixz.chatmod.ChatMod.LOGGER;

public class FileEmpty {
    public static boolean isFileEmpty(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            LOGGER.warn("File {} does not exist.", fileName);
            return file.length() != 0L;
        } else {
            return true;
        }
    }
}
