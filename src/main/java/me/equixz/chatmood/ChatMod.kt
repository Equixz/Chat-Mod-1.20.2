package me.equixz.chatmood

import me.equixz.chatmood.structure.FileCreation
import me.equixz.chatmood.structure.LoadData
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class ChatMod : ModInitializer {
    override fun onInitialize() {
        FileCreation.createFile("Crazy")
        FileCreation.createFile("Uwu")
        var fileName = "Crazy.txt"
        var rawLink = "https://pastebin.com/raw/D4qNzZEU"
        if (!isFileEmpty(fileName)) {
            LOGGER.info("File {} is not empty. Skipping download.", fileName)
        } else {
            LoadData.downloadFile(fileName, rawLink)
        }

        fileName = "Uwu.txt"
        rawLink = "https://pastebin.com/raw/zrv1X1SH"
        if (!isFileEmpty(fileName)) {
            LOGGER.info("File {} is not empty. Skipping download.", fileName)
        } else {
            LoadData.downloadFile(fileName, rawLink)
        }
    }

    private fun isFileEmpty(fileName: String): Boolean {
        val filePath = "config/ChatMod/Files/"
        val file = File(filePath, fileName)
        if (file.exists()) {
            return file.length() == 0L
        } else {
            // Handle case where file doesn't exist
            LOGGER.warn("File {} does not exist.", fileName)
            return false
        }
    }

    companion object {
        private const val MOD_ID: String = "chat-mod"
        val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
    }
}
