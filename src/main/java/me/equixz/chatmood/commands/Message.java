package me.equixz.chatmood.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.equixz.chatmood.structure.ListFilesInFolder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static me.equixz.chatmood.functions.MessageFunctions.*;

public class Message {
    private static final String FOLDER_PATH = "config/ChatMod/Files";

    public static void registerCommands() {
        registerBaseCommand();
    }

    private static void registerBaseCommand() {
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
                .then(ClientCommandManager.literal("prefix")
                    .then(ClientCommandManager.literal("p")
                        .executes(context -> {
                            changePrefix("/p ");
                            return 0;
                        })
                    )
                    .then(ClientCommandManager.literal("party")
                        .executes(context -> {
                            changePrefix("/p ");
                            return 0;
                        })
                    )
                    .then(ClientCommandManager.literal("g")
                        .executes(context -> {
                            changePrefix("/g ");
                            return 0;
                        })
                    )
                    .then(ClientCommandManager.literal("guild")
                        .executes(context -> {
                            changePrefix("/g ");
                            return 0;
                        })
                    )
                    .then(ClientCommandManager.literal("r")
                        .executes(context -> {
                            changePrefix("/r ");
                            return 0;
                        })
                    )
                    .then(ClientCommandManager.literal("reply")
                        .executes(context -> {
                            changePrefix("/r");
                            return 0;
                        })
                    )
                    .executes(context -> {
                        changePrefix("");
                        return 0;
                    })
                );
            dispatcher.register(baseCommand);
        });
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
            context.getSource().sendError(Text.of("The specified file does not exist."));
        }

        return 0; // Return the appropriate result
    }

    private static boolean fileExistsInFolder(String fileName) {
        // Check if the file exists in the folder
        List<String> fileNames = ListFilesInFolder.listFilesWithoutExtension(FOLDER_PATH);
        return fileNames.contains(fileName);
    }
}