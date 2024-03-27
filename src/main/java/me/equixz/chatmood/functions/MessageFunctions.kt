package me.equixz.chatmood.functions;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.equixz.chatmood.ChatModClient.*;
import static me.equixz.chatmood.structure.FileReader.doesFileExist;
import static me.equixz.chatmood.structure.FileReader.readFiles;

public class MessageFunctions {

    public static void sendMessage(String prefix, String message) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(prefix + message);
        }
    }

    public static void changeBombBellPrefix(CommandContext<FabricClientCommandSource> context, String newPrefix) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        receiveMessage.bombBellPrefix = newPrefix;
        if (newPrefix.length() <= 15) {
            if (newPrefix.isEmpty()) {
                context.getSource().sendFeedback(Text.literal("Please provide a non-empty message!").formatted(Formatting.RED));
                return;
            }
            if (player != null)
                player.sendMessage(Text.literal("Bomb Bell prefix changed to: " + newPrefix).formatted(Formatting.GREEN), false);
        } else {
            context.getSource().sendFeedback(Text.literal("Please provide a prefix that's under 15 characters!").formatted(Formatting.RED));
        }

    }

    public static void changeMessage(String newMessage) {
        messageToSend = newMessage;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (newMessage.isEmpty() && player != null) {
            player.sendMessage(Text.literal("Please provide an existing file name!").formatted(Formatting.RED), false);
            return;
        }
        if (player != null)

            player.sendMessage(Text.literal("File output changed to: " + newMessage).formatted(Formatting.GREEN), false);
    }

    public static void changeCooldown(CommandContext<FabricClientCommandSource> context, String newCooldown) {
        int cooldown = Integer.parseInt(newCooldown);
        if (cooldown < 1250) {
            context.getSource().sendError(Text.of("Cooldown value must be 1250 or higher."));
            return;
        }

        delayIncrement = cooldown;
        initialDelay = cooldown;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (newCooldown.isEmpty()) {
            context.getSource().sendFeedback(Text.literal("Please provide a non-empty message!").formatted(Formatting.RED));
            return;
        }
        if (player != null)
            player.sendMessage(Text.literal("Message cooldown changed to: " + newCooldown).formatted(Formatting.GREEN), false);
    }

    public static void changePrefix(String newPrefix) {
        prefixToUse = newPrefix;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null)
            player.sendMessage(Text.literal("Message chat changed to: " + newPrefix).formatted(Formatting.GREEN), false);

    }

    public static void changePrefixBombbell(String newPrefix) {
        prefixBombbellToUse = newPrefix;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null)
            player.sendMessage(Text.literal("Bomb Bell chat changed to: " + newPrefix).formatted(Formatting.GREEN), false);

    }

    public static void sendChatMessage() {
        // Get the file name based on the message
        String fileName = messageToSend + ".txt"; // Adjust the file name based on naming convention

        // Check if the file exists
        if (!doesFileExist(fileName)) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.sendMessage(Text.literal("The file doesn't exist.").formatted(Formatting.RED), false);
            }
            return;
        }
        // Read the lines from the file
        List<String> lines = readFiles(fileName);

        // Create a ScheduledExecutorService
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        // Schedule tasks with increasing delays
        long currentDelay = initialDelay;
        for (String line : lines) {
            final String message = line;
            executorService.schedule(() -> {
                try {
                    sendMessage(prefixToUse, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }}, currentDelay, TimeUnit.MILLISECONDS);
            currentDelay += delayIncrement;
        }
        // Shutdown the executor service after all tasks are done
        executorService.shutdown();
    }

}
