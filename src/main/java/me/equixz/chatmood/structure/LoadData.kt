package me.equixz.chatmood.structure;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import static me.equixz.chatmood.ChatMod.LOGGER;

public class LoadData {

    // Define a constant for the folder path
    private static final String pathToFolder = "config/ChatMod/Files/";

    public static void downloadFile(String fileName, String rawLink) {
        // Construct the full file path
        String filePath = pathToFolder + fileName;

        try {
            URL url = new URL(rawLink);
            URLConnection connection = url.openConnection();
            try (InputStream inputStream = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 FileWriter writer = new FileWriter(filePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.write("\n"); // Add newline character after each line
                }
                LOGGER.info("File downloaded successfully: " + filePath);
            } catch (IOException e) {
                LOGGER.warn("Error writing to file: " + e.getMessage());
            }
        } catch (IOException e) {
            LOGGER.warn("Error downloading file: " + e.getMessage());
        }
    }
}