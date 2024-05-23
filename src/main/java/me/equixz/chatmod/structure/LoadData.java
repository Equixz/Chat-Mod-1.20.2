package me.equixz.chatmod.structure;

import me.equixz.chatmod.ChatMod;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class LoadData {

    public static void downloadFile(String fileName, String rawLink) {
        try {
            URL url = new URL(rawLink);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                 FileWriter writer = new FileWriter(fileName)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.write("\n");
                }
                ChatMod.LOGGER.info("File downloaded successfully: " + fileName);
            } catch (IOException e) {
                ChatMod.LOGGER.warn("Error writing to file: " + e.getMessage());
            }
        } catch (IOException e) {
            ChatMod.LOGGER.warn("Error downloading file: " + e.getMessage());
        }
    }
}