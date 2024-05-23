package me.equixz.chatmod.config;

import com.google.gson.Gson;
import net.minecraft.client.MinecraftClient;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {

    public static class ConfigData {
        public boolean bombBellState;
        public boolean combatXpBombEnabled;
        public boolean professionXpBombEnabled;
        public boolean professionSpeedBombEnabled;
        public boolean dungeonBombEnabled;
        public boolean lootBombEnabled;
        public boolean legalToggle;
        public int initialDelay;
        public String messageToSend;
        public String prefixToUse;
        public String prefixBombbellToUse;
        public String bombBellPrefix;
        public String lastBombType;
        public String lastBombWorld;

        public ConfigData(boolean bombBellState, boolean combatXpBombEnabled, boolean professionXpBombEnabled, boolean professionSpeedBombEnabled, boolean dungeonBombEnabled, boolean lootBombEnabled, boolean legalToggle, int initialDelay, String messageToSend, String prefixToUse, String prefixBombbellToUse, String bombBellPrefix, String lastBombType, String lastBombWorld) {
            this.bombBellState = bombBellState;
            this.combatXpBombEnabled = combatXpBombEnabled;
            this.professionXpBombEnabled = professionXpBombEnabled;
            this.professionSpeedBombEnabled = professionSpeedBombEnabled;
            this.dungeonBombEnabled = dungeonBombEnabled;
            this.lootBombEnabled = lootBombEnabled;
            this.legalToggle = legalToggle;
            this.initialDelay = initialDelay;
            this.messageToSend = messageToSend;
            this.prefixToUse = prefixToUse;
            this.prefixBombbellToUse = prefixBombbellToUse;
            this.bombBellPrefix = bombBellPrefix;
            this.lastBombType = lastBombType;
            this.lastBombWorld = lastBombWorld;
        }

        public void save() {
            try {
                Gson gson = new Gson();
                FileWriter writer = new FileWriter(configFile.toFile());
                gson.toJson(this, writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static ConfigData getDefault() {
            return new ConfigData(
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    true,
                    1250,
                    "Crazy",
                    "",
                    "/g ",
                    "|",
                    "Party Bomb",
                    "1"
            );
        }

        public static void toggleFeature(String featureName, boolean newValue) {
            ConfigData configData = getConfigData();
            try {
                if (featureName != null) {
                    ConfigData.class.getDeclaredField(featureName).setBoolean(configData, newValue);
                }
                configData.save();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static final Path configDir = Paths.get(MinecraftClient.getInstance().runDirectory.getPath() + "/config/ChatMod");
    private static final Path configFile = Paths.get(configDir + "/config.json");
    private static ConfigData configData = null;

    public static ConfigData getConfigData() {
        if (configData != null) return configData;
        try {
            if (!Files.exists(configFile)) {
                Files.createDirectories(configDir);
                Files.createFile(configFile);
                configData = ConfigData.getDefault();
                configData.save();
                return configData;
            }
        } catch (IOException e) {
            e.printStackTrace();
            configData = ConfigData.getDefault();
            return configData;
        }
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(configFile.toFile());
            configData = gson.fromJson(reader, ConfigData.class);
        } catch (IOException e) {
            e.printStackTrace();
            configData = ConfigData.getDefault();
        }
        return configData;
    }

    public static void toggleFeature(String featureName, boolean newValue) {
        ConfigData.toggleFeature(featureName, newValue);
    }
}
