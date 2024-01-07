package me.equixz.chatmood.structure;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class FileList {
    public static List<String> listFilesInDirectory(String directoryPath) {
        List<String> fileNames = new ArrayList<>();
        Path dir = Paths.get(directoryPath);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                fileNames.add(entry.getFileName().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNames;
    }
}
