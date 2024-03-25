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
        registerCustomCommands();
    }

    private static void registerBaseCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> baseCommand = ClientCommandManager.literal("cmsg")
                    .executes(context -> {
                        // Provide usage feedback to the player if no argument is provided
                        context.getSource().sendFeedback(Text.of("Usage: /cmsg <file>"));
                        return 0;
                    });

            dispatcher.register(baseCommand.then(
                    ClientCommandManager.argument("file", StringArgumentType.word())
                            .suggests(Message::fileSuggestions) // Suggests file names for completion
                            .executes(Message::executeCommand) // Execute the command with the provided file
            ));
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

    public static void registerCustomCommands() {
        System.out.println("Loading ccd, cpf & cbpf command");
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("ccd")
                .then(ClientCommandManager.argument("message", IntegerArgumentType.integer())
                    .executes(context -> {
                        String customMessage = String.valueOf(IntegerArgumentType.getInteger(context, "message"));
                        changeCooldown(context, customMessage);
                        return 0;
                    })
                )
        ));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("cbpf")
                .then(ClientCommandManager.argument("message", StringArgumentType.greedyString()) // Changed argument type to greedyString
                    .executes(context -> {
                        String customMessage = StringArgumentType.getString(context, "message");
                        changeBombBellPrefix(context, customMessage);
                        return 0;
                    })
                )
        ));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("cpf")
                .then(ClientCommandManager.literal("p")  // Added a sub-command "default"
                    .executes(context -> {
                        // If no argument is provided, use a default message
                        changePrefix("/p ");
                        return 0;
                    })
                ).then(ClientCommandManager.literal("party")  // Added a sub-command "default"
                    .executes(context -> {
                        // If no argument is provided, use a default message
                            changePrefix("/p ");
                            return 0;
                    })
                ).then(ClientCommandManager.literal("g")  // Added a sub-command "default"
                    .executes(context -> {
                        // If no argument is provided, use a default message
                            changePrefix("/g ");
                            return 0;
                    })
                ).then(ClientCommandManager.literal("guild")  // Added a sub-command "default"
                    .executes(context -> {
                        // If no argument is provided, use a default message
                        changePrefix("/g ");
                        return 0;
                    })
                ).then(ClientCommandManager.literal("r")  // Added a sub-command "default"
                    .executes(context -> {
                        // If no argument is provided, use a default message
                        changePrefix("/r ");
                        return 0;
                    })
                ).then(ClientCommandManager.literal("reply")  // Added a sub-command "default"
                    .executes(context -> {
                        // If no argument is provided, use a default message
                        changePrefix("/r ");
                        return 0;
                    })
                ).executes(context -> {
                    // If no argument is provided, use a default message
                    changePrefix("");
                    return 0;
                })
        ));
    }
}