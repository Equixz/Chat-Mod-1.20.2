package me.equixz.chatmod.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.equixz.chatmod.config.Config;
import me.equixz.chatmod.functions.message.bombbellPrefix;
import me.equixz.chatmod.structure.ListFilesInFolder;
import me.equixz.chatmod.structure.LoadData;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static me.equixz.chatmod.functions.message.cooldown.changeCooldown;
import static me.equixz.chatmod.functions.message.message.*;
import static me.equixz.chatmod.functions.message.prefix.*;
import static me.equixz.chatmod.functions.message.prefixBombbell.changePrefixBombbell;

public class Message {
    private static final String FOLDER_PATH = "config/ChatMod/Files";

    public static void registerBaseCommand() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
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
                            changeCooldown(Integer.toString(customTime));
                            return 0;
                        })
                    )
                )
                .then(ClientCommandManager.literal("bombbellprefix")
                    .then(ClientCommandManager.argument("prefix", StringArgumentType.greedyString())
                        .executes(context -> {
                            String customPrefix = StringArgumentType.getString(context, "prefix");
                            bombbellPrefix.changeBombBellPrefix(customPrefix);
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
                                assert player != null;
                                player.sendMessage(Text.literal("Invalid syntax. Usage: /chat import <fileName> <URL>").formatted(Formatting.GREEN), false);
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
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        LiteralArgumentBuilder<FabricClientCommandSource> featuresCommand = ClientCommandManager.literal(action);
        for (java.lang.reflect.Field field : Config.ConfigData.class.getDeclaredFields()) {
            if (field.getType() == boolean.class) {
                String fieldName = field.getName();
                featuresCommand.then(ClientCommandManager.literal(fieldName)
                    .executes(context -> {
                        boolean newValue = action.equals("enable");
                        boolean currentValue = getCurrentValue(fieldName);
                        if (newValue == currentValue) {
                            player.sendMessage(Text.literal("Feature " + fieldName + " is already " + (newValue ? "enabled" : "disabled")).formatted(Formatting.RED), false);
                        } else {
                            Config.toggleFeature(fieldName, newValue);
                            player.sendMessage(Text.literal("Feature " + fieldName + " " + (newValue ? "enabled" : "disabled")).formatted(newValue ? Formatting.GREEN : Formatting.RED), false);
                        }
                        return 0;
                    })
                );
            }
        }
        featuresCommand.executes(context -> {
            player.sendMessage(Text.literal("Please specify a feature to " + action).formatted(Formatting.RED), false);
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
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return false;
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
                        changePrefix(prefix);
                    } else if (prefixType.equals("bombbell")) {
                        changePrefixBombbell(prefix);
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
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        String fileName = StringArgumentType.getString(context, "file");
        if (fileExistsInFolder(fileName)) {
            changeMessage(fileName);
        } else {
            player.sendMessage(Text.literal("The specified file does not exist.").formatted(Formatting.RED), false);
        }

        return 0;
    }

    private static boolean fileExistsInFolder(String fileName) {
        List<String> fileNames = ListFilesInFolder.listFilesWithoutExtension(FOLDER_PATH);
        return fileNames.contains(fileName);
    }
}
