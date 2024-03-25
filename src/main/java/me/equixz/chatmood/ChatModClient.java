package me.equixz.chatmood;

import com.mojang.brigadier.CommandDispatcher;
import me.equixz.chatmood.commands.Message;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.Objects;

import static me.equixz.chatmood.functions.MessageFunctions.sendChatMessage;
import static me.equixz.chatmood.functions.MessageFunctions.sendMessage;
import static net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK;

public class ChatModClient implements ClientModInitializer {

    private static final KeyBinding sendMessageKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("Send Group Messages", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "ChatMod")
    );
    private static ChatModClient instance;
    public static String messageToSend = "Crazy";
    public static String prefixToUse = "";
    public static int initialDelay = 1000;
    public static String bombBellPrefix = "Snow Bell";
    public static int delayIncrement = 1000;

    @Override
    public void onInitializeClient() {
        CommandDispatcher<FabricClientCommandSource> dispatcher = new CommandDispatcher<>();
        Message.registerCommands();
        END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        if (sendMessageKey.wasPressed()) {
            sendChatMessage();
        }
    }

    public void receiveMessage(Text message) {
        if(Config.getConfigData().BombBellState){
            // Convert Text to unformatted string (removing formatting codes)
            String unformattedMessage = message.getString().replaceAll("(?i)§[0-9A-FK-ORXa-fk-orx]", "");

            // Check if the unformatted message starts with "[Bomb Bell]"
            String bombType = null;
            String wcNumber = null;
            if (unformattedMessage.startsWith("[Bomb Bell]")) {
                // Remove the "[Bomb Bell]" substring from the message
                unformattedMessage = unformattedMessage.substring("[Bomb Bell]".length()).trim();

                // Find the index of "thrown a" in the message
                int thrownIndex = unformattedMessage.indexOf("thrown a");
                if (thrownIndex != -1) {
                    // Remove the "thrown a" substring from the message
                    unformattedMessage = unformattedMessage.substring(thrownIndex + "thrown a".length()).trim();
                }
                // Split the message into bomb type and WC number
                String[] parts = unformattedMessage.split("on WC");
                if (parts.length == 2) {
                    bombType = parts[0].trim();
                    wcNumber = parts[1].trim();
                }
                if(Objects.equals(bombType, "Combat XP Bomb") && Config.getConfigData().combatXpBombEnabled){
                    sendMessage("/g ", bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                }
                if(Objects.equals(bombType, "Profession XP Bomb") && Config.getConfigData().professionXpBombEnabled){
                    sendMessage("/g ", bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                }
                if(Objects.equals(bombType, "Profession Speed Bomb") && Config.getConfigData().professionSpeedBombEnabled){
                    sendMessage("/g ", bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                }
                if(Objects.equals(bombType, "Dungeon Bomb") && Config.getConfigData().dungeonBombEnabled){
                    sendMessage("/g ", bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                }
                if(Objects.equals(bombType, "Loot Bomb") && Config.getConfigData().lootBombEnabled){
                    sendMessage("/g ", bombBellPrefix + " A " + bombType + " has been thrown on world " + wcNumber);
                }

            }
        }
    }

    public static ChatModClient getInstance() {
        if (instance == null) {
            instance = new ChatModClient();
        }
        return instance;
    }

}
