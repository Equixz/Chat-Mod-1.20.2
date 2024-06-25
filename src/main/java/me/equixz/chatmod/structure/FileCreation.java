package me.equixz.chatmod.structure;

import me.equixz.chatmod.ChatMod;

import java.io.File;
import java.io.IOException;

public class FileCreation {
    public static void createFile(String newFileName) {
        String folderPath = "config/ChatMod/Files";
        String fileName = newFileName + ".txt";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (!success) {
                ChatMod.LOGGER.info("Failed to create the folder.");
                return;
            }
        }
        File file = new File(fileName);
        try {
            boolean fileCreated = file.createNewFile();

            if (fileCreated) {
                ChatMod.LOGGER.info("File created successfully: {}", file.getAbsolutePath());
            } else {
                ChatMod.LOGGER.info("File already exists: {}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            ChatMod.LOGGER.info("An error occurred while creating the file: {}", e.getMessage());
        }
    }
}
