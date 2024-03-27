package me.equixz.chatmood.config

import com.google.gson.Gson
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Config : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent: Screen? -> createConfigScreen(parent) }
    }

    class ConfigData(
        var bombBellState: Boolean,
        var combatXpBombEnabled: Boolean,
        var professionXpBombEnabled: Boolean,
        var professionSpeedBombEnabled: Boolean,
        var dungeonBombEnabled: Boolean,
        var lootBombEnabled: Boolean
    ) {
        fun save() {
            try {
                val gson = Gson()
                val writer = FileWriter(configFile.toFile())
                gson.toJson(this, writer)
                writer.flush()
                writer.close()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        companion object {
            val default: ConfigData
                get() = ConfigData(
                    bombBellState = true,
                    combatXpBombEnabled = true,
                    professionXpBombEnabled = true,
                    professionSpeedBombEnabled = true,
                    dungeonBombEnabled = true,
                    lootBombEnabled = true
                ) // Change the default value as needed

            fun toggleFeature(featureName: String?, newValue: Boolean) {
                val configData = getConfigData()
                try {
                    featureName?.let { ConfigData::class.java.getDeclaredField(it) }?.setBoolean(configData, newValue)
                    configData!!.save()
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        private val configDir: Path = Paths.get(MinecraftClient.getInstance().runDirectory.path + "/config/ChatMod")
        private val configFile: Path = Paths.get("$configDir/config.json")
        private var configData: ConfigData? = null

        fun getConfigData(): ConfigData? {
            if (configData != null) return configData

            try {
                if (!Files.exists(configFile)) {
                    Files.createDirectories(configDir)
                    Files.createFile(configFile)
                    configData = ConfigData.default
                    configData!!.save()
                    return configData
                }
            } catch (e: IOException) {
                e.printStackTrace()
                configData = ConfigData.default
                return configData
            }
            try {
                val gson = Gson()
                val reader = FileReader(configFile.toFile())
                configData = gson.fromJson(reader, ConfigData::class.java)
            } catch (e: IOException) {
                e.printStackTrace()
                configData = ConfigData.default
            }
            return configData
        }

        fun createConfigScreen(parent: Screen?): Screen {
            val builder =
                ConfigBuilder.create().setParentScreen(parent).setTitle(Text.translatable("screen.config.title"))

            val general = builder.getOrCreateCategory(Text.translatable("screen.general.config"))

            general.addEntry(builder.entryBuilder()
                .startBooleanToggle(Text.translatable("screen.feature.enable"), configData!!.bombBellState)
                .setDefaultValue(true).setTooltip(Text.of("Enable/Disable Bomb Bell"))
                .setSaveConsumer { newValue: Boolean -> configData!!.bombBellState = newValue }
                .build())

            general.addEntry(builder.entryBuilder().startBooleanToggle(
                Text.translatable("screen.feature.combat_xp_bomb.enable"),
                configData!!.combatXpBombEnabled
            )
                .setDefaultValue(true).setTooltip(Text.of("Enable/Disable Combat XP Bomb"))
                .setSaveConsumer { newValue: Boolean -> configData!!.combatXpBombEnabled = newValue }
                .build())

            general.addEntry(builder.entryBuilder().startBooleanToggle(
                Text.translatable("screen.feature.prof_xp_bomb.enable"),
                configData!!.professionXpBombEnabled
            )
                .setDefaultValue(true).setTooltip(Text.of("Enable/Disable Profession XP Bomb"))
                .setSaveConsumer { newValue: Boolean -> configData!!.professionXpBombEnabled = newValue }
                .build())

            general.addEntry(builder.entryBuilder().startBooleanToggle(
                Text.translatable("screen.feature.prof_speed_bomb.enable"),
                configData!!.professionSpeedBombEnabled
            )
                .setDefaultValue(true).setTooltip(Text.of("Enable/Disable Profession Speed Bomb"))
                .setSaveConsumer { newValue: Boolean -> configData!!.professionSpeedBombEnabled = newValue }
                .build())

            general.addEntry(builder.entryBuilder().startBooleanToggle(
                Text.translatable("screen.feature.dungeon_bomb.enable"),
                configData!!.dungeonBombEnabled
            )
                .setDefaultValue(true).setTooltip(Text.of("Enable/Disable Dungeon Bomb"))
                .setSaveConsumer { newValue: Boolean -> configData!!.dungeonBombEnabled = newValue }
                .build())

            general.addEntry(builder.entryBuilder().startBooleanToggle(
                Text.translatable("screen.feature.loot_bomb.enable"),
                configData!!.lootBombEnabled
            )
                .setDefaultValue(true).setTooltip(Text.of("Enable/Disable Loot Bomb"))
                .setSaveConsumer { newValue: Boolean -> configData!!.lootBombEnabled = newValue }
                .build())

            builder.setSavingRunnable { configData!!.save() }

            return builder.build()
        }

        fun toggleFeature(featureName: String?, newValue: Boolean) {
            ConfigData.toggleFeature(featureName, newValue)
        }
    }
}
