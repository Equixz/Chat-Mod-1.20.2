package me.equixz.chatmod.functions;

import me.equixz.chatmod.config.Config;
import me.equixz.chatmod.functions.message.lastBombbell;
import net.minecraft.text.Text;

/* data when thrown on the same world as the player
[CHAT] &7Want to thank &f{USERNAME}&7? &b&nClick here to thank them!
[CHAT] &b{USERNAME}&3 has thrown a &bLoot Bomb&3! The entire server gets &bdouble loot &3for &b20 minutes&3!
 */


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
                        Save(parts);
                    } else if ((parts[0].contains("Profession XP") && configData.professionXpBombEnabled)) {
                        Save(parts);
                    } else if ((parts[0].contains("Profession Speed") && configData.professionSpeedBombEnabled)) {
                        Save(parts);
                    } else if ((parts[0].contains("Dungeon") && configData.dungeonBombEnabled)) {
                        Save(parts);
                    } else if ((parts[0].contains("Loot") && configData.lootBombEnabled)) {
                        Save(parts);
                    }
                }
                if (!configData.legalToggle) {
                    lastBombbell.sendLastBombbell();
                }
            }
        }
    }

    private void Save(String[] parts) {
        Config.ConfigData configData = Config.getConfigData();
        configData.lastBombType = parts[0].trim();
        configData.lastBombWorld = parts[1].trim();
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
