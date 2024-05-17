package me.equixz.chatmood.functions;

import com.mojang.brigadier.context.CommandContext;
import me.equixz.chatmood.ChatMod;
import me.equixz.chatmood.config.Config;
import me.equixz.chatmood.structure.FileReader;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
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

    public static void changeBombBellPrefix(CommandContext<FabricClientCommandSource> context, String newPrefix) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (newPrefix.length() <= 15) {
            if (newPrefix.isEmpty()) {
                context.getSource().sendFeedback(Text.literal("Please provide a non-empty message!").formatted(Formatting.RED));
                return;
            }
            assert player != null;
            player.sendMessage(Text.literal("Bomb Bell prefix changed to: " + newPrefix).formatted(Formatting.GREEN), false);
            Config.ConfigData configData = Config.getConfigData();
            if (configData != null) {
                configData.bombBellPrefix = newPrefix;
                configData.save();
            }
        } else {
            context.getSource().sendFeedback(Text.literal("Please provide a prefix that's under 15 characters!").formatted(Formatting.RED));
        }
    }

    public static void changeMessage(String newMessage) {
        Config.ConfigData configData = Config.getConfigData();
        if (configData != null) {
            configData.messageToSend = newMessage;
            configData.save();
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (newMessage.isEmpty() && player != null) {
                player.sendMessage(Text.literal("Please provide an existing file name!").formatted(Formatting.RED), false);
            } else if (player != null) {
                player.sendMessage(Text.literal("File output changed to: " + newMessage).formatted(Formatting.GREEN), false);
            }
        }
    }

    public static void changeCooldown(CommandContext<FabricClientCommandSource> context, String newCooldown) {
        int cooldown = Integer.parseInt(newCooldown);
        if (cooldown < 1250) {
            context.getSource().sendError(Text.of("Cooldown value must be 1250 or higher."));
            return;
        }
        Config.ConfigData configData = Config.getConfigData();
        if (configData != null) {
            configData.initialDelay = cooldown;
            configData.save();
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (newCooldown.isEmpty() && player != null) {
                player.sendMessage(Text.literal("Please provide a non-empty message!").formatted(Formatting.RED), false);
            } else if (player != null) {
                player.sendMessage(Text.literal("Message cooldown changed to: " + newCooldown).formatted(Formatting.GREEN), false);
            }
        }
    }

    public static void changePrefix(String newPrefix) {
        Config.ConfigData configData = Config.getConfigData();
        if (configData != null) {
            configData.prefixToUse = newPrefix;
            configData.save();
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.sendMessage(Text.literal("Message chat changed to: " + newPrefix).formatted(Formatting.GREEN), false);
            }
        }
    }

    public static void changePrefixBombbell(String newPrefix) {
        Config.ConfigData configData = Config.getConfigData();
        if (configData != null) {
            configData.prefixBombbellToUse = newPrefix;
            configData.save();
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.sendMessage(Text.literal("Bomb Bell chat changed to: " + newPrefix).formatted(Formatting.GREEN), false);
            }
        }
    }

    public static void sendLastBombbell() {
        Config.ConfigData configData = Config.getConfigData();
        if (configData != null) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            String prefixBombbellToUse = configData.prefixBombbellToUse;
            String bombBellPrefix = configData.bombBellPrefix;
            String bombType = configData.lastBombType;
            String wcNumber = configData.lastBombWorld;
            if (prefixBombbellToUse.isEmpty() && player != null) {
                player.sendMessage(Text.literal("Please send your log file to a developer.").formatted(Formatting.RED), false);
                ChatMod.LOGGER.error("prefixBombbellToUse is empty.");
                configData.prefixBombbellToUse = "/g ";
                return;
            }
            if (bombBellPrefix.isEmpty() && player != null) {
                player.sendMessage(Text.literal("Please send your log file to a developer.").formatted(Formatting.RED), false);
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
    }

    public static void switchToLatestBombbell() {
        Config.ConfigData configData = Config.getConfigData();
        if (configData != null) {
            String wcNumber = configData.lastBombWorld;
            if (!wcNumber.isEmpty()) {
                sendMessage("/switch ", wcNumber);
            }
        }
    }

    public static void sendChatMessage() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (isSendingMessage) {
            if (player != null) {
                player.sendMessage(Text.literal("A message is already being sent. Please wait.").formatted(Formatting.RED), false);
                isPressed = true;
            }
            return;
        }
        isSendingMessage = true;
        String fileName = Config.getConfigData().messageToSend + ".txt";
        if (!FileReader.doesFileExist(fileName)) {
            player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.sendMessage(Text.literal("The file doesn't exist.").formatted(Formatting.RED), false);
            }
            isSendingMessage = false;
            return;
        }

        List<String> lines = FileReader.readFiles(fileName);

        java.util.concurrent.ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        long currentDelay = Config.getConfigData().initialDelay;
        for (String line : lines) {
            ClientPlayerEntity finalPlayer = player;
            executorService.schedule(() -> {
                try {
                    if (isPressed) {
                        executorService.shutdownNow();
                        isSendingMessage = false;
                        isPressed = false;
                        assert finalPlayer != null;
                        finalPlayer.sendMessage(Text.literal("Message sending interrupted.").formatted(Formatting.RED), false);
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
