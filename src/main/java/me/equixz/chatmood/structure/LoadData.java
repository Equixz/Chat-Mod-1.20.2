package me.equixz.chatmood.structure;

import me.equixz.chatmood.ChatMod;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class LoadData {
    private static final String PATH_TO_FOLDER = "config/ChatMod/Files/";

    public static void downloadFile(String fileName, String rawLink) {
        String filePath = PATH_TO_FOLDER + fileName;

        try {
            URL url = new URL(rawLink);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                 FileWriter writer = new FileWriter(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.write("\n");
                }
                ChatMod.LOGGER.info("File downloaded successfully: " + filePath);
            } catch (IOException e) {
                ChatMod.LOGGER.warn("Error writing to file: " + e.getMessage());
            }
        } catch (IOException e) {
            ChatMod.LOGGER.warn("Error downloading file: " + e.getMessage());
        }
    }
}