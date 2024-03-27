package me.equixz.chatmood.structure

import me.equixz.chatmood.ChatMod
import java.io.BufferedReader
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

object LoadData {
    // Define a constant for the folder path
    private const val PATH_TO_FOLDER = "config/ChatMod/Files/"

    fun downloadFile(fileName: String, rawLink: String?) {
        // Construct the full file path
        val filePath = PATH_TO_FOLDER + fileName

        try {
            val url = URL(rawLink)
            val connection = url.openConnection()
            try {
                connection.getInputStream().use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        FileWriter(filePath).use { writer ->
                            var line: String?
                            while ((reader.readLine().also { line = it }) != null) {
                                line?.let { writer.write(it) }
                                writer.write("\n") // Add newline character after each line
                            }
                            ChatMod.LOGGER.info("File downloaded successfully: $filePath")
                        }
                    }
                }
            } catch (e: IOException) {
                ChatMod.LOGGER.warn("Error writing to file: " + e.message)
            }
        } catch (e: IOException) {
            ChatMod.LOGGER.warn("Error downloading file: " + e.message)
        }
    }
}