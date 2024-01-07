package me.equixz.chatmood.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import static me.equixz.chatmood.functions.MessageFunctions.*;

public class Message {

    public static void registerCommands() {
        // Register your client commands here using the dispatcher
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("cmsg")
                        .then(ClientCommandManager.literal("crazy")  // Added a sub-command "default"
                                .executes(context -> {
                                    // If no argument is provided, use a default message
                                    changeMessage(context, "Crazy");
                                    return 0;
                                })
                        )
        ));

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
                        )
                        .then(ClientCommandManager.literal("party")  // Added a sub-command "default"
                                .executes(context -> {
                                    // If no argument is provided, use a default message
                                    changePrefix("/p ");
                                    return 0;
                                })
                        )
                        .then(ClientCommandManager.literal("g")  // Added a sub-command "default"
                                .executes(context -> {
                                    // If no argument is provided, use a default message
                                    changePrefix("/g ");
                                    return 0;
                                })
                        )
                        .then(ClientCommandManager.literal("guild")  // Added a sub-command "default"
                                .executes(context -> {
                                    // If no argument is provided, use a default message
                                    changePrefix("/g ");
                                    return 0;
                                })
                        )
                        .then(ClientCommandManager.literal("r")  // Added a sub-command "default"
                                .executes(context -> {
                                    // If no argument is provided, use a default message
                                    changePrefix("/r ");
                                    return 0;
                                })
                        )
                        .then(ClientCommandManager.literal("reply")  // Added a sub-command "default"
                                .executes(context -> {
                                    // If no argument is provided, use a default message
                                    changePrefix("/r ");
                                    return 0;
                                })
                        )
                        .executes(context -> {
                            // If no argument is provided, use a default message
                            changePrefix("");
                            return 0;
                        })
        ));

    }
}
