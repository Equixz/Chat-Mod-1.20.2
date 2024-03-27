package me.equixz.chatmood.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.equixz.chatmood.config.Config;
import me.equixz.chatmood.structure.ListFilesInFolder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static me.equixz.chatmood.functions.MessageFunctions.*;
import static me.equixz.chatmood.structure.LoadData.downloadFile;

public class Message {
    private static final String FOLDER_PATH = "config/ChatMod/Files";

    public static void registerBaseCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> baseCommand = ClientCommandManager.literal("chat")
                .then(ClientCommandManager.literal("message")
                    .then(ClientCommandManager.argument("file", StringArgumentType.word())
                        .suggests(Message::fileSuggestions)
                        .executes(Message::executeCommand)
                    )
                )
                .then(ClientCommandManager.literal("cooldown")
                    .then(ClientCommandManager.argument("time", IntegerArgumentType.integer())
                        .executes(context -> {
                            int customTime = IntegerArgumentType.getInteger(context, "time");
                            changeCooldown(context, String.valueOf(customTime));
                            return 0;
                        })
                    )
                )
                .then(ClientCommandManager.literal("bombbellprefix")
                    .then(ClientCommandManager.argument("prefix", StringArgumentType.greedyString())
                        .executes(context -> {
                            String customPrefix = StringArgumentType.getString(context, "prefix");
                            changeBombBellPrefix(context, customPrefix);
                            return 0;
                        })
                    )
                )
                .then(ClientCommandManager.literal("import")
                    .then(ClientCommandManager.argument("File", StringArgumentType.word())
                        .then(ClientCommandManager.argument("Raw Link", StringArgumentType.word())
                            .executes(context -> {
                                String fileName = StringArgumentType.getString(context, "File");
                                String rawLink = StringArgumentType.getString(context, "Raw Link");
                                downloadFile(fileName, rawLink);
                                return 0;
                            })
                        )
                    )
                )
                .then(ClientCommandManager.literal("messagechat")
                    .then(buildDynamicPrefixCommand("chat"))
                    .then(buildDynamicPrefixCommand("bombbell"))
                )
                .then(ClientCommandManager.literal("feature")
                    .then(features("enable"))
                    .then(features("disable"))
                );
            dispatcher.register(baseCommand);
        });
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> features(String action) {
        LiteralArgumentBuilder<FabricClientCommandSource> featuresCommand = ClientCommandManager.literal(action);

        for (Field field : Config.ConfigData.class.getDeclaredFields()) {
            if (field.getType() == boolean.class) {
                String fieldName = field.getName();
                featuresCommand.then(ClientCommandManager.literal(fieldName)
                        .executes(context -> {
                            boolean newValue = action.equals("enable");
                            boolean currentValue = getCurrentValue(fieldName);

                            if (newValue == currentValue) {
                                // Feature is already in the desired state
                                context.getSource().sendFeedback(Text.literal("Feature " + fieldName + " is already " + (newValue ? "enabled" : "disabled")).formatted(Formatting.RED));
                            } else {
                                // Toggle the feature
                                Config.toggleFeature(fieldName, newValue);
                                context.getSource().sendFeedback(Text.literal("Feature " + fieldName + " " + (newValue ? "enabled" : "disabled")).formatted(Formatting.GREEN));
                            }
                            return 0;
                        })
                );
            }
        }

        // Add subcommand for handling when no feature is specified
        featuresCommand.executes(context -> {
            context.getSource().sendFeedback(Text.literal("Please specify a feature to " + action).formatted(Formatting.RED));
            return 0;
        });
        return featuresCommand;
    }

    private static boolean getCurrentValue(String fieldName) {
        try {
            Field field = Config.ConfigData.class.getDeclaredField(fieldName);
            field.setAccessible(true); // Ensure private fields can be accessed
            return field.getBoolean(Config.getConfigData());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false; // Handle errors gracefully, return default value
        }
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildDynamicPrefixCommand(String prefixType) {
        LiteralArgumentBuilder<FabricClientCommandSource> dynamicPrefixCommand = ClientCommandManager.literal(prefixType)
            .executes(context -> {
                if (prefixType.equals("chat")) {
                    changePrefix("");
                } else if (prefixType.equals("bombbell")) {
                    changePrefixBombbell("/g ");
                }
                return 0;
            });

        List<String> commands = Arrays.asList("p", "party", "g", "guild", "r", "reply", "n", "normal");
        for (String command : commands) {
            dynamicPrefixCommand = dynamicPrefixCommand.then(ClientCommandManager.literal(command)
                .executes(context -> {
                    String prefix = "";
                    switch (command) {
                        case "p", "party" -> prefix = "/p ";
                        case "g", "guild" -> prefix = "/g ";
                        case "r", "reply" -> prefix = "/r ";
                        case "n", "normal" -> prefix = "";
                    }
                    if (prefixType.equals("chat")) {
                        changePrefix(prefix);
                    } else if (prefixType.equals("bombbell")) {
                        changePrefixBombbell(prefix);
                    }
                    return 0;
                }));
        }

        return dynamicPrefixCommand;
    }

    private static CompletableFuture<Suggestions> fileSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        // Get the list of file names
        List<String> fileNames = ListFilesInFolder.listFilesWithoutExtension(FOLDER_PATH);

        // Suggest file names
        for (String fileName : fileNames) {
            builder.suggest(fileName);
        }

        return builder.buildFuture();
    }

    private static int executeCommand(CommandContext<FabricClientCommandSource> context) {
        String fileName = StringArgumentType.getString(context, "file");
        // Check if the file exists in the folder
        if (fileExistsInFolder(fileName)) {
            // If the file exists, execute the command
            changeMessage(fileName);
        } else {
            // If the file doesn't exist, provide feedback to the player
            context.getSource().sendFeedback(Text.literal("The specified file does not exist.").formatted(Formatting.RED));
        }

        return 0; // Return the appropriate result
    }

    private static boolean fileExistsInFolder(String fileName) {
        // Check if the file exists in the folder
        List<String> fileNames = ListFilesInFolder.listFilesWithoutExtension(FOLDER_PATH);
        return fileNames.contains(fileName);
    }
}