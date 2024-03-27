package me.equixz.chatmood.config;

import com.google.gson.Gson;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config implements ModMenuApi {
    private static final Path configDir = Paths.get(MinecraftClient.getInstance().runDirectory.getPath() + "/config/ChatMod");
    private static final Path configFile = Paths.get(configDir + "/config.json");
    private static ConfigData configData;

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

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.translatable("screen.config.title"));

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("screen.general.config"));

        general.addEntry(builder.entryBuilder().startBooleanToggle(Text.translatable("screen.feature.enable"), configData.BombBellState)
                .setDefaultValue(true).setTooltip(Text.of("Enable/Disable Bomb Bell")).setSaveConsumer(newValue -> configData.BombBellState = newValue).build());

        general.addEntry(builder.entryBuilder().startBooleanToggle(Text.translatable("screen.feature.combat_xp_bomb.enable"), configData.combatXpBombEnabled)
                .setDefaultValue(true).setTooltip(Text.of("Enable/Disable Combat XP Bomb")).setSaveConsumer(newValue -> configData.combatXpBombEnabled = newValue).build());

        general.addEntry(builder.entryBuilder().startBooleanToggle(Text.translatable("screen.feature.prof_xp_bomb.enable"), configData.professionXpBombEnabled)
                .setDefaultValue(true).setTooltip(Text.of("Enable/Disable Profession XP Bomb")).setSaveConsumer(newValue -> configData.professionXpBombEnabled = newValue).build());

        general.addEntry(builder.entryBuilder().startBooleanToggle(Text.translatable("screen.feature.prof_speed_bomb.enable"), configData.professionSpeedBombEnabled)
                .setDefaultValue(true).setTooltip(Text.of("Enable/Disable Profession Speed Bomb")).setSaveConsumer(newValue -> configData.professionSpeedBombEnabled = newValue).build());

        general.addEntry(builder.entryBuilder().startBooleanToggle(Text.translatable("screen.feature.dungeon_bomb.enable"), configData.dungeonBombEnabled)
                .setDefaultValue(true).setTooltip(Text.of("Enable/Disable Dungeon Bomb")).setSaveConsumer(newValue -> configData.dungeonBombEnabled = newValue).build());

        general.addEntry(builder.entryBuilder().startBooleanToggle(Text.translatable("screen.feature.loot_bomb.enable"), configData.lootBombEnabled)
                .setDefaultValue(true).setTooltip(Text.of("Enable/Disable Loot Bomb")).setSaveConsumer(newValue -> configData.lootBombEnabled = newValue).build());

        builder.setSavingRunnable(configData::save);

        return builder.build();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return Config::createConfigScreen;
    }

    public static class ConfigData {
        public boolean BombBellState;
        public boolean combatXpBombEnabled;
        public boolean professionXpBombEnabled;
        public boolean professionSpeedBombEnabled;
        public boolean dungeonBombEnabled;
        public boolean lootBombEnabled;

        public ConfigData(boolean featureEnabled, boolean combatXpBombEnabled, boolean professionXpBombEnabled, boolean professionSpeedBombEnabled,
                          boolean dungeonBombEnabled, boolean lootBombEnabled) {
            this.BombBellState = featureEnabled;
            this.combatXpBombEnabled = combatXpBombEnabled;
            this.professionXpBombEnabled = professionXpBombEnabled;
            this.professionSpeedBombEnabled = professionSpeedBombEnabled;
            this.dungeonBombEnabled = dungeonBombEnabled;
            this.lootBombEnabled = lootBombEnabled;
        }

        public static ConfigData getDefault() {
            return new ConfigData(true, true, true, true, true, true); // Change the default value as needed
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

        public static void toggleFeature(String featureName, boolean newValue) {
            ConfigData configData = getConfigData();
            try {
                Field field = ConfigData.class.getDeclaredField(featureName);
                field.setBoolean(configData, newValue);
                configData.save();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    public static void toggleFeature(String featureName, boolean newValue) {
        ConfigData.toggleFeature(featureName, newValue);
    }

}
