package me.equixz.chatmood.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import me.equixz.chatmood.config.Config
import me.equixz.chatmood.functions.MessageFunctions
import me.equixz.chatmood.structure.ListFilesInFolder
import me.equixz.chatmood.structure.LoadData
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.concurrent.CompletableFuture

object Message {
    private const val FOLDER_PATH = "config/ChatMod/Files"

    fun registerBaseCommand() {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher: CommandDispatcher<FabricClientCommandSource?>, _: CommandRegistryAccess? ->
            val baseCommand = ClientCommandManager.literal("chat")
                .then(ClientCommandManager.literal("message")
                    .then(ClientCommandManager.argument("file", StringArgumentType.word())
                        .suggests { _, builder -> fileSuggestions(builder) }
                        .executes { context -> executeCommand(context) }
                    )
                )
                .then(ClientCommandManager.literal("cooldown")
                    .then(ClientCommandManager.argument("time", IntegerArgumentType.integer())
                        .executes { context: CommandContext<FabricClientCommandSource> ->
                            val customTime = IntegerArgumentType.getInteger(context, "time")
                            MessageFunctions.changeCooldown(context, customTime.toString())
                            0
                        }
                    )
                )
                .then(ClientCommandManager.literal("bombbellprefix")
                    .then(ClientCommandManager.argument("prefix", StringArgumentType.greedyString())
                        .executes { context: CommandContext<FabricClientCommandSource> ->
                            val customPrefix = StringArgumentType.getString(context, "prefix")
                            MessageFunctions.changeBombBellPrefix(context, customPrefix)
                            0
                        }
                    )
                )
                .then(ClientCommandManager.literal("import")
                    .then(ClientCommandManager.argument("fileNameAndUrl", StringArgumentType.greedyString())
                        .executes { context: CommandContext<FabricClientCommandSource> ->
                            val fileNameAndUrl = StringArgumentType.getString(context, "fileNameAndUrl")
                            // Split the fileNameAndUrl into fileName and URL
                            val parts = fileNameAndUrl.split(" ".toRegex(), limit = 2).toTypedArray()
                            if (parts.size == 2) {
                                val fileName = parts[0]
                                val rawLink = parts[1]
                                LoadData.downloadFile(fileName, rawLink)
                                return@executes 1
                            } else {
                                context.source.sendError(Text.of("Invalid syntax. Usage: /chat import <fileName> <URL>"))
                                return@executes 0
                            }
                        }
                    )
                )
                .then(
                    ClientCommandManager.literal("messagechat")
                        .then(buildDynamicPrefixCommand("chat"))
                        .then(buildDynamicPrefixCommand("bombbell"))
                )
                .then(
                    ClientCommandManager.literal("feature")
                        .then(features("enable"))
                        .then(features("disable"))
                )
            dispatcher.register(baseCommand)
        })
    }

    private fun features(action: String): LiteralArgumentBuilder<FabricClientCommandSource> {
        val featuresCommand = ClientCommandManager.literal(action)

        for (field in Config.ConfigData::class.java.declaredFields) {
            if (field.type == Boolean::class.javaPrimitiveType) {
                val fieldName = field.name
                featuresCommand.then(ClientCommandManager.literal(fieldName)
                    .executes { context: CommandContext<FabricClientCommandSource> ->
                        val newValue = action == "enable"
                        val currentValue = getCurrentValue(fieldName)

                        if (newValue == currentValue) {
                            // Feature is already in the desired state
                            context.source.sendFeedback(
                                Text.literal("Feature " + fieldName + " is already " + (if (newValue) "enabled" else "disabled"))
                                    .formatted(
                                        Formatting.RED
                                    )
                            )
                        } else {
                            // Toggle the feature
                            Config.toggleFeature(fieldName, newValue)
                            context.source.sendFeedback(
                                Text.literal("Feature " + fieldName + " " + (if (newValue) "enabled" else "disabled"))
                                    .formatted(
                                        Formatting.GREEN
                                    )
                            )
                        }
                        0
                    }
                )
            }
        }

        // Add subcommand for handling when no feature is specified
        featuresCommand.executes { context: CommandContext<FabricClientCommandSource> ->
            context.source.sendFeedback(
                Text.literal(
                    "Please specify a feature to $action"
                ).formatted(Formatting.RED)
            )
            0
        }
        return featuresCommand
    }

    private fun getCurrentValue(fieldName: String): Boolean {
        try {
            val field = Config.ConfigData::class.java.getDeclaredField(fieldName)
            field.isAccessible = true // Ensure private fields can be accessed
            return field.getBoolean(Config.getConfigData())
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
            return false // Handle errors gracefully, return default value
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            return false
        }
    }

    private fun buildDynamicPrefixCommand(prefixType: String): LiteralArgumentBuilder<FabricClientCommandSource?> {
        var dynamicPrefixCommand = ClientCommandManager.literal(prefixType)
            .executes { _: CommandContext<FabricClientCommandSource?>? ->
                if (prefixType == "chat") {
                    MessageFunctions.changePrefix("")
                } else if (prefixType == "bombbell") {
                    MessageFunctions.changePrefixBombbell("/g ")
                }
                0
            }

        val commands: List<String> = mutableListOf("p", "party", "g", "guild", "r", "reply", "n", "normal")
        for (command in commands) {
            dynamicPrefixCommand = dynamicPrefixCommand.then(ClientCommandManager.literal(command)
                .executes {
                    var prefix = ""
                    when (command) {
                        "p", "party" -> prefix = "/p "
                        "g", "guild" -> prefix = "/g "
                        "r", "reply" -> prefix = "/r "
                        "n", "normal" -> prefix = ""
                    }
                    if (prefixType == "chat") {
                        MessageFunctions.changePrefix(prefix)
                    } else if (prefixType == "bombbell") {
                        MessageFunctions.changePrefixBombbell(prefix)
                    }
                    0
                })
        }

        return dynamicPrefixCommand
    }

    private fun fileSuggestions(
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions?> {
        // Get the list of file names
        val fileNames = ListFilesInFolder.listFilesWithoutExtension(FOLDER_PATH)

        // Suggest file names
        for (fileName in fileNames) {
            builder.suggest(fileName)
        }

        return builder.buildFuture()
    }

    private fun executeCommand(context: CommandContext<FabricClientCommandSource>): Int {
        val fileName = StringArgumentType.getString(context, "file")
        // Check if the file exists in the folder
        if (fileExistsInFolder(fileName)) {
            // If the file exists, execute the command
            MessageFunctions.changeMessage(fileName)
        } else {
            // If the file doesn't exist, provide feedback to the player
            context.source.sendFeedback(Text.literal("The specified file does not exist.").formatted(Formatting.RED))
        }

        return 0 // Return the appropriate result
    }

    private fun fileExistsInFolder(fileName: String): Boolean {
        // Check if the file exists in the folder
        val fileNames = ListFilesInFolder.listFilesWithoutExtension(FOLDER_PATH)
        return fileNames.contains(fileName)
    }
}