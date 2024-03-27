package me.equixz.chatmood.structure;

import java.io.File;
import static me.equixz.chatmood.ChatMod.LOGGER;

public class FileCreation {

    public static void createFile(String newFileName) {
        // Specify the folder path
        String folderPath = "config/ChatMod/Files";

        // Specify the file name
        String fileName = newFileName + ".txt";

        // Create a File object for the folder
        File folder = new File(folderPath);

        // Check if the folder exists, and create it if not
        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (!success) {
                LOGGER.info("Failed to create the folder.");
                return;
            }
        }

        // Create a File object for the file within the folder
        File file = new File(folder, fileName);

        try {
            // Create the file
            boolean fileCreated = file.createNewFile();

            if (fileCreated) {
                LOGGER.info("File created successfully: " + file.getAbsolutePath());
            } else {
                LOGGER.info("File already exists: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            LOGGER.info("An error occurred while creating the file: " + e.getMessage());
        }
    }
}


