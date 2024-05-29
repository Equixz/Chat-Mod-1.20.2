package me.equixz.chatmod.functions.message;

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
import static me.equixz.chatmod.functions.message.sendChatMessage.sendMessage;

public class chatMessage {
    private static boolean isSendingMessage = false;
    private static boolean isPressed = false;
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
                    //noinspection CallToPrintStackTrace
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
