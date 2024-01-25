package me.equixz.chatmood;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.equixz.chatmood.structure.FileCreation.createFile;

public class ChatMod implements ModInitializer {
	public static final String MOD_ID = "chat-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("YO SHIT HEAD IF YOU SEE THIS U GAY ASF!!");
		createFile("Crazy");
		createFile("Uwu");
	}
}