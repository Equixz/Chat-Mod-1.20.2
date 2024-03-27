package me.equixz.chatmood.structure

import me.equixz.chatmood.ChatMod
import java.io.File

object FileCreation {
    fun createFile(newFileName: String) {
        // Specify the folder path
        val folderPath = "config/ChatMod/Files"

        // Specify the file name
        val fileName = "$newFileName.txt"

        // Create a File object for the folder
        val folder = File(folderPath)

        // Check if the folder exists, and create it if not
        if (!folder.exists()) {
            val success = folder.mkdirs()
            if (!success) {
                ChatMod.LOGGER.info("Failed to create the folder.")
                return
            }
        }

        // Create a File object for the file within the folder
        val file = File(folder, fileName)

        try {
            // Create the file
            val fileCreated = file.createNewFile()

            if (fileCreated) {
                ChatMod.LOGGER.info("File created successfully: " + file.absolutePath)
            } else {
                ChatMod.LOGGER.info("File already exists: " + file.absolutePath)
            }
        } catch (e: Exception) {
            ChatMod.LOGGER.info("An error occurred while creating the file: " + e.message)
        }
    }
}


