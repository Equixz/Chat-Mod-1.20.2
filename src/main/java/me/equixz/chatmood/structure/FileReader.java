package me.equixz.chatmood.structure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static me.equixz.chatmood.ChatMod.LOGGER;

public class FileReader {

    public static List<String> readFiles(String readFileName) {
        // Specify the folder path
        String folderPath = "config/chatmod/";
        // Create a Path object for the file
        Path filePath = Paths.get(folderPath, readFileName);

        try {
            // Return the list of lines
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            LOGGER.info("An error occurred while reading the file: " + e.getMessage());
            return List.of(); // Return an empty list or handle appropriately
        }
    }

    public static boolean doesFileExist(String fileName) {
        // Specify the folder path
        String folderPath = "config/chatmod/";

        // Create a Path object for the file
        Path filePath = Paths.get(folderPath, fileName);

        // Check if the file exists
        return Files.exists(filePath);
    }
}
