package me.equixz.chatmod.structure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListFilesInFolder {
    public static List<String> listFilesWithoutExtension(String folderPath) {
        List<String> fileNamesWithoutExtension = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
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
        return (lastDotIndex != -1) ? fileName.substring(0, lastDotIndex) : fileName;
    }
}
