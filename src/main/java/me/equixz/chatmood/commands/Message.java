package me.equixz.chatmood.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.equixz.chatmood.structure.ListFilesInFolder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.List;

import static me.equixz.chatmood.functions.MessageFunctions.*;

public class Message {
    private static final String FOLDER_PATH = "config/ChatMod";
    private static LiteralCommandNode<FabricClientCommandSource> baseCommand;
    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        registerBaseCommand(dispatcher);
        registerCustomCommands();
    }

    private static void registerBaseCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        // Base command "/cmsg"
        baseCommand = ClientCommandManager.literal("cmsg").build();
        dispatcher.getRoot().addChild(baseCommand);
        refreshSubcommands(dispatcher);
    }

    private static void refreshSubcommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        // Get the list of file names
        List<String> fileNames = ListFilesInFolder.listFilesWithoutExtension(FOLDER_PATH);

        // Clear existing subcommands
        baseCommand.getChildren().forEach(child -> dispatcher.getRoot().getChild(child.getName()).getChild(String.valueOf(child)));

        // Dynamically add subcommands based on the file names
        for (String fileName : fileNames) {
            // Create a literal subcommand for each file name
            LiteralCommandNode<FabricClientCommandSource> subCommand = ClientCommandManager.literal(fileName)
                    .executes(context -> {
                        // Code to execute when the subcommand is used
                        changeMessage(context, fileName);
                        return 0;
                    })
                    .build();

            // Attach the subcommand to the base command
            baseCommand.addChild(subCommand);
        }
    }

    public static void registerCustomCommands() {
        // Add your custom commands here
        // Example:
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