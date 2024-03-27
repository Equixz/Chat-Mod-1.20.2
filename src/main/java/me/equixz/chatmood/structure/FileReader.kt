package me.equixz.chatmood.structure

import me.equixz.chatmood.ChatMod
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

object FileReader {
    fun readFiles(readFileName: String?): List<String> {
        // Specify the folder path
        val folderPath = "config/ChatMod/Files"
        // Create a Path object for the file
        val filePath = Paths.get(folderPath, readFileName)

        try {
            // Return the list of lines
            return Files.readAllLines(filePath)
        } catch (e: IOException) {
            ChatMod.LOGGER.info("An error occurred while reading the file: " + e.message)
            return listOf() // Return an empty list or handle appropriately
        }
    }

    fun doesFileExist(fileName: String?): Boolean {
        // Specify the folder path
        val folderPath = "config/ChatMod/Files"

        // Create a Path object for the file
        val filePath = Paths.get(folderPath, fileName)

        // Check if the file exists
        return Files.exists(filePath)
    }
}
