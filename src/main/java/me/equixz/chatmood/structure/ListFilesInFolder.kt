package me.equixz.chatmood.structure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListFilesInFolder {
    public static List<String> listFilesWithoutExtension(String folderPath) {
        List<String> fileNamesWithoutExtension = new ArrayList<>();

        // Create a File object for the folder
        File folder = new File(folderPath);

        // Check if the path is a directory
        if (folder.isDirectory()) {
            // Get the list of files in the folder
            File[] files = folder.listFiles();

            // Check if there are any files
            if (files != null) {
                for (File file : files) {
                    // Get the file name without extension
                    String fileNameWithoutExtension = getFileNameWithoutExtension(file);
                    fileNamesWithoutExtension.add(fileNameWithoutExtension);
                }
            }
        }

        return fileNamesWithoutExtension;
    }

    private static String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        } else {
            return fileName;
        }
    }
}