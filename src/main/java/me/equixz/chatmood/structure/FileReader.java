package me.equixz.chatmood.structure;

import me.equixz.chatmood.ChatMod;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    public static List<String> readFiles(String readFileName) {
        String folderPath = "config/ChatMod/Files";
        Path filePath = Paths.get(folderPath, readFileName);

        try {
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            ChatMod.LOGGER.info("An error occurred while reading the file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static boolean doesFileExist(String fileName) {
        String folderPath = "config/ChatMod/Files";

        Path filePath = Paths.get(folderPath, fileName);

        return Files.exists(filePath);
    }
}
