package me.equixz.chatmood;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static me.equixz.chatmood.structure.FileCreation.createFile;
import static me.equixz.chatmood.structure.LoadData.downloadFile;

public class ChatMod implements ModInitializer {
	public static final String MOD_ID = "chat-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		createFile("Crazy");
		createFile("Uwu");
		String fileName = "Crazy.txt";
		String rawLink = "https://pastebin.com/raw/D4qNzZEU";
		if (!isFileEmpty(fileName)) {
			LOGGER.info("File {} is not empty. Skipping download.", fileName);
		} else {
			downloadFile(fileName, rawLink);
		}

		fileName = "Uwu.txt";
		rawLink = "https://pastebin.com/raw/zrv1X1SH";
		if (!isFileEmpty(fileName)) {
			LOGGER.info("File {} is not empty. Skipping download.", fileName);
		} else {
			downloadFile(fileName, rawLink);
		}
	}

	private boolean isFileEmpty(String fileName) {
		String filePath = "config/ChatMod/Files/";
		File file = new File(filePath, fileName);
		if (file.exists()) {
			return file.length() == 0;
		} else {
			// Handle case where file doesn't exist
			LOGGER.warn("File {} does not exist.", fileName);
			return false;
		}
	}
}
