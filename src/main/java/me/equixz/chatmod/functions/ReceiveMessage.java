package me.equixz.chatmod.functions;

import me.equixz.chatmod.config.Config;
import me.equixz.chatmod.functions.message.lastBombbell;
import net.minecraft.text.Text;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReceiveMessage {
    public void receiveMessages(Text message) {
        Config.ConfigData configData = Config.getConfigData();
        if (configData != null && configData.bombBellState) {
            String unformattedMessage = message.getString().replaceAll("(?i)§[0-9A-FK-ORXa-fk-orx]", "");
            if (unformattedMessage.startsWith("[Bomb Bell]")) {
                unformattedMessage = unformattedMessage.substring("[Bomb Bell]".length()).trim();
                int thrownIndex = unformattedMessage.indexOf("thrown a");
                if (thrownIndex != -1) {
                    unformattedMessage = unformattedMessage.substring(thrownIndex + "thrown a".length()).trim();
                }
                String[] parts = unformattedMessage.split("on WC");
                if (parts.length == 2) {
                    if ((parts[0].contains("Combat XP") && configData.combatXpBombEnabled)) {
                        Save(parts[0].trim(), parts[1].trim());
                    } else if ((parts[0].contains("Profession XP") && configData.professionXpBombEnabled)) {
                        Save(parts[0].trim(), parts[1].trim());
                    } else if ((parts[0].contains("Profession Speed") && configData.professionSpeedBombEnabled)) {
                        Save(parts[0].trim(), parts[1].trim());
                    } else if ((parts[0].contains("Dungeon") && configData.dungeonBombEnabled)) {
                        Save(parts[0].trim(), parts[1].trim());
                    } else if ((parts[0].contains("Loot") && configData.lootBombEnabled)) {
                        Save(parts[0].trim(), parts[1].trim());
                    }
                }
            } else if (unformattedMessage.contains("The entire server gets")) {
                unformattedMessage = unformattedMessage.replaceAll("!", "");
                String[] keywords = {"Combat XP", "Profession XP", "Profession Speed", "Dungeon", "Loot"};
                String patternString = "\\b(" + String.join("|", keywords) + ")\\b";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(unformattedMessage);
                String world = Optional.ofNullable(extractWCNumber.extractWC()).orElse("0");
                String bomb = matcher.group() + " Bomb";
                if ((matcher.group().contains("Combat XP") && configData.combatXpBombEnabled)) {
                    Save(bomb, world);
                } else if ((matcher.group().contains("Profession XP") && configData.professionXpBombEnabled)) {
                    Save(bomb, world);
                } else if ((matcher.group().contains("Profession Speed") && configData.professionSpeedBombEnabled)) {
                    Save(bomb, world);
                } else if ((matcher.group().contains("Dungeon") && configData.dungeonBombEnabled)) {
                    Save(bomb, world);
                } else if ((matcher.group().contains("Loot") && configData.lootBombEnabled)) {
                    Save(bomb, world);
                }
            }
            if (!configData.legalToggle) {
                lastBombbell.sendLastBombbell();
            }
        }
    }

    private void Save(String type, String world) {
        Config.ConfigData configData = Config.getConfigData();
        configData.lastBombType = type;
        configData.lastBombWorld = world;
        configData.save();
    }

    private static ReceiveMessage instance;

    public static ReceiveMessage getReceiveMessageInstance() {
        if (instance == null) {
            instance = new ReceiveMessage();
        }
        return instance;
    }
}