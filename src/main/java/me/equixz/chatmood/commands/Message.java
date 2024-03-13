package me.equixz.chatmood.commands;

import com.mojang.brigadier.CommandDispatcher;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static me.equixz.chatmood.functions.MessageFunctions.changeCooldown;
import static me.equixz.chatmood.functions.MessageFunctions.changePrefix;

public class Message {
    private static final String FOLDER_PATH = "config/ChatMod";

    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        registerBaseCommand(dispatcher);
        registerCustomCommands();
    }

    private static void registerBaseCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        // Base command "/cmsg"
        System.out.println("Loading base command");
        LiteralArgumentBuilder<FabricClientCommandSource> baseCommand = ClientCommandManager.literal("cmsg");
        dispatcher.register(baseCommand);

        // Add a file argument with dynamic suggestions
        baseCommand.then(ClientCommandManager.argument("file", StringArgumentType.word())
                .suggests(Message::fileSuggestions)
                .executes(Message::executeCommand));
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
        // Logic based on the selected file name
        System.out.println("Selected file: " + fileName);
        return 0; // Return the appropriate result
    }

    public static void registerCustomCommands() {
        // Add your custom commands here
        System.out.println("Loading ccd & cpf command");
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