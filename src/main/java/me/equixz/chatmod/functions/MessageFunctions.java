package me.equixz.chatmod.functions;

import me.equixz.chatmod.ChatMod;
import me.equixz.chatmod.config.Config;
import me.equixz.chatmod.structure.FileReader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MessageFunctions {
    private static boolean isSendingMessage = false;
    private static boolean isPressed = false;

    public static void sendMessage(String prefix, String message) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(prefix + message);
        }
    }

    public static void changeBombBellPrefix(String newPrefix) {
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        if (newPrefix.length() <= 15) {
            if (newPrefix.isEmpty()) {
                player.sendMessage(Text.literal("Please provide a non-empty message!").formatted(Formatting.RED), false);
                return;
            }
            player.sendMessage(Text.literal("Bomb Bell prefix changed to: " + newPrefix).formatted(Formatting.GREEN), false);
            Config.ConfigData configData = Config.getConfigData();
            configData.bombBellPrefix = newPrefix;
            configData.save();
        } else {
            player.sendMessage(Text.literal("Please provide a prefix that's under 15 characters!").formatted(Formatting.RED), false);
        }
    }

    public static void changeMessage(String newMessage) {
        Config.ConfigData configData = Config.getConfigData();
        configData.messageToSend = newMessage;
        configData.save();
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        if (newMessage.isEmpty()) {
            player.sendMessage(Text.literal("Please provide an existing file name!").formatted(Formatting.RED), false);
        } else {
            player.sendMessage(Text.literal("File output changed to: " + newMessage).formatted(Formatting.GREEN), false);
        }
    }

    public static void changeCooldown(String newCooldown) {
        int cooldown = Integer.parseInt(newCooldown);
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        if (cooldown < 1250) {
            player.sendMessage(Text.literal("Cooldown value must be 1250 or higher.").formatted(Formatting.RED), false);
            return;
        }
        Config.ConfigData configData = Config.getConfigData();
        configData.initialDelay = cooldown;
        configData.save();
        if (newCooldown.isEmpty()) {
            player.sendMessage(Text.literal("Please provide a non-empty message!").formatted(Formatting.RED), false);
        } else {
            player.sendMessage(Text.literal("Message cooldown changed to: " + newCooldown).formatted(Formatting.GREEN), false);
        }
    }

    public static void changePrefix(String newPrefix) {
        Config.ConfigData configData = Config.getConfigData();
        configData.prefixToUse = newPrefix;
        configData.save();
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        player.sendMessage(Text.literal("Message chat changed to: " + newPrefix).formatted(Formatting.GREEN), false);
    }

    public static void changePrefixBombbell(String newPrefix) {
        Config.ConfigData configData = Config.getConfigData();
        configData.prefixBombbellToUse = newPrefix;
        configData.save();
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        player.sendMessage(Text.literal("Bomb Bell chat changed to: " + newPrefix).formatted(Formatting.GREEN), false);
    }

    public static void sendLastBombbell() {
        Config.ConfigData configData = Config.getConfigData();
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        String prefixBombbellToUse = configData.prefixBombbellToUse;
        String bombBellPrefix = configData.bombBellPrefix;
        String bombType = configData.lastBombType;
        String wcNumber = configData.lastBombWorld;
        if (prefixBombbellToUse.isEmpty()) {
            player.sendMessage(Text.literal("Please send your log file to a developer. Error #2452").formatted(Formatting.RED), false);
            ChatMod.LOGGER.error(prefixBombbellToUse);
            ChatMod.LOGGER.error("prefixBombbellToUse is empty.");
            configData.prefixBombbellToUse = "/g ";
            return;
        }
        if (bombBellPrefix.isEmpty()) {
            player.sendMessage(Text.literal("Please send your log file to a developer. Error #2453").formatted(Formatting.RED), false);
            ChatMod.LOGGER.error(bombBellPrefix);
            ChatMod.LOGGER.error("bombBellPrefix is empty.");
            configData.bombBellPrefix = "|";
            return;
        }
        if (!bombType.isEmpty()) {
            switch (bombType) {
                case "Combat XP Bomb":
                    if (configData.combatXpBombEnabled) {
                        sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                    }
                    break;
                case "Profession XP Bomb":
                    if (configData.professionXpBombEnabled) {
                        sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                    }
                    break;
                case "Profession Speed Bomb":
                    if (configData.professionSpeedBombEnabled) {
                        sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                    }
                    break;
                case "Dungeon Bomb":
                    if (configData.dungeonBombEnabled) {
                        sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                    }
                    break;
                case "Loot Bomb":
                    if (configData.lootBombEnabled) {
                        sendMessage(prefixBombbellToUse, bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                    }
                    break;
            }
        }
    }

    public static void switchToLatestBombbell() {
        Config.ConfigData configData = Config.getConfigData();
        String wcNumber = configData.lastBombWorld;
        if (wcNumber != null) {
            sendMessage("/switch ", wcNumber);
        }
    }

    public static void sendChatMessage() {
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        if (isSendingMessage) {
            player.sendMessage(Text.literal("A message is already being sent. Please wait.").formatted(Formatting.RED), false);
            isPressed = true;
            return;
        }
        isSendingMessage = true;
        String fileName = Config.getConfigData().messageToSend + ".txt";
        if (!FileReader.doesFileExist(fileName)) {
            player.sendMessage(Text.literal("The file doesn't exist.").formatted(Formatting.RED), false);
            isSendingMessage = false;
            return;
        }

        List<String> lines = FileReader.readFiles(fileName);

        java.util.concurrent.ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        long currentDelay = Config.getConfigData().initialDelay;
        for (String line : lines) {
            executorService.schedule(() -> {
                try {
                    if (isPressed) {
                        executorService.shutdownNow();
                        isSendingMessage = false;
                        isPressed = false;
                        player.sendMessage(Text.literal("Message sending interrupted.").formatted(Formatting.RED), false);
                        return;
                    }
                    sendMessage(Config.getConfigData().prefixToUse, line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, currentDelay, TimeUnit.MILLISECONDS);
            currentDelay += Config.getConfigData().initialDelay;
        }

        executorService.schedule(() -> {
            executorService.shutdown();
            isSendingMessage = false;
            isPressed = false;
        }, currentDelay, TimeUnit.MILLISECONDS);
    }
}
