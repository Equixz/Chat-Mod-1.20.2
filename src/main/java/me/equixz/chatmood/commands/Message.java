package me.equixz.chatmood.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.equixz.chatmood.config.Config;
import me.equixz.chatmood.functions.MessageFunctions;
import me.equixz.chatmood.structure.ListFilesInFolder;
import me.equixz.chatmood.structure.LoadData;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Message {
    private static final String FOLDER_PATH = "config/ChatMod/Files";

    public static void registerBaseCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> baseCommand = ClientCommandManager.literal("chat")
                    .then(ClientCommandManager.literal("message")
                            .then(ClientCommandManager.argument("file", StringArgumentType.word())
                                    .suggests((context, builder) -> fileSuggestions(builder))
                                    .executes(Message::executeCommand)
                            )
                    )
                    .then(ClientCommandManager.literal("cooldown")
                            .then(ClientCommandManager.argument("time", IntegerArgumentType.integer())
                                    .executes(context -> {
                                        int customTime = IntegerArgumentType.getInteger(context, "time");
                                        MessageFunctions.changeCooldown(context, Integer.toString(customTime));
                                        return 0;
                                    })
                            )
                    )
                    .then(ClientCommandManager.literal("bombbellprefix")
                            .then(ClientCommandManager.argument("prefix", StringArgumentType.greedyString())
                                    .executes(context -> {
                                        String customPrefix = StringArgumentType.getString(context, "prefix");
                                        MessageFunctions.changeBombBellPrefix(context, customPrefix);
                                        return 0;
                                    })
                            )
                    )
                    .then(ClientCommandManager.literal("import")
                            .then(ClientCommandManager.argument("fileNameAndUrl", StringArgumentType.greedyString())
                                    .executes(context -> {
                                        String fileNameAndUrl = StringArgumentType.getString(context, "fileNameAndUrl");
                                        String[] parts = fileNameAndUrl.split(" ", 2);
                                        if (parts.length == 2) {
                                            String fileName = parts[0];
                                            String rawLink = parts[1];
                                            LoadData.downloadFile("config/ChatMod/Files/" + fileName, rawLink);
                                            return 1;
                                        } else {
                                            context.getSource().sendError(Text.of("Invalid syntax. Usage: /chat import <fileName> <URL>"));
                                            return 0;
                                        }
                                    })
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

        for (java.lang.reflect.Field field : Config.ConfigData.class.getDeclaredFields()) {
            if (field.getType() == boolean.class) {
                String fieldName = field.getName();
                featuresCommand.then(ClientCommandManager.literal(fieldName)
                        .executes(context -> {
                            boolean newValue = action.equals("enable");
                            boolean currentValue = getCurrentValue(fieldName);
                            if (newValue == currentValue) {
                                context.getSource().sendFeedback(Text.literal("Feature " + fieldName + " is already " + (newValue ? "enabled" : "disabled")).formatted(Formatting.RED));
                            } else {
                                Config.toggleFeature(fieldName, newValue);
                                context.getSource().sendFeedback(Text.literal("Feature " + fieldName + " " + (newValue ? "enabled" : "disabled")).formatted(newValue ? Formatting.GREEN : Formatting.RED));
                            }
                            return 0;
                        })
                );
            }
        }
        featuresCommand.executes(context -> {
            context.getSource().sendFeedback(Text.literal("Please specify a feature to " + action).formatted(Formatting.RED));
            return 0;
        });
        return featuresCommand;
    }

    private static boolean getCurrentValue(String fieldName) {
        try {
            java.lang.reflect.Field field = Config.ConfigData.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getBoolean(Config.getConfigData());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildDynamicPrefixCommand(String prefixType) {
        LiteralArgumentBuilder<FabricClientCommandSource> dynamicPrefixCommand = ClientCommandManager.literal(prefixType)
                .executes(context -> {
                    if (prefixType.equals("chat")) {
                        MessageFunctions.changePrefix("");
                    } else if (prefixType.equals("bombbell")) {
                        MessageFunctions.changePrefixBombbell("/g ");
                    }
                    return 0;
                });

        List<String> commands = List.of("p", "party", "g", "guild", "r", "reply", "n", "normal");
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
                            MessageFunctions.changePrefix(prefix);
                        } else if (prefixType.equals("bombbell")) {
                            MessageFunctions.changePrefixBombbell(prefix);
                        }
                        return 0;
                    })
            );
        }
        return dynamicPrefixCommand;
    }

    private static CompletableFuture<Suggestions> fileSuggestions(SuggestionsBuilder builder) {
        List<String> fileNames = ListFilesInFolder.listFilesWithoutExtension(FOLDER_PATH);
        for (String fileName : fileNames) {
            builder.suggest(fileName);
        }

        return builder.buildFuture();
    }

    private static int executeCommand(CommandContext<FabricClientCommandSource> context) {
        String fileName = StringArgumentType.getString(context, "file");
        if (fileExistsInFolder(fileName)) {
            MessageFunctions.changeMessage(fileName);
        } else {
            context.getSource().sendFeedback(Text.literal("The specified file does not exist.").formatted(Formatting.RED));
        }

        return 0;
    }

    private static boolean fileExistsInFolder(String fileName) {
        List<String> fileNames = ListFilesInFolder.listFilesWithoutExtension(FOLDER_PATH);
        return fileNames.contains(fileName);
    }
}
