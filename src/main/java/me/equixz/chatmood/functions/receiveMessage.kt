package me.equixz.chatmood.functions

import me.equixz.chatmood.config.Config
import net.minecraft.text.Text

class receiveMessage {
    fun receiveMessages(message: Text) {
        if (Config.getConfigData()!!.bombBellState) {
            // Convert Text to unformatted string (removing formatting codes)
            var unformattedMessage = message.string.replace("(?i)§[0-9A-FK-ORXa-fk-orx]".toRegex(), "")

            // Check if the unformatted message starts with "[Bomb Bell]"
            var bombType: String? = null
            var wcNumber: String? = null
            if (unformattedMessage.startsWith("[Bomb Bell]")) {
                // Remove the "[Bomb Bell]" substring from the message
                unformattedMessage = unformattedMessage.substring("[Bomb Bell]".length).trim { it <= ' ' }

                // Find the index of "thrown a" in the message
                val thrownIndex = unformattedMessage.indexOf("thrown a")
                if (thrownIndex != -1) {
                    // Remove the "thrown a" substring from the message
                    unformattedMessage =
                        unformattedMessage.substring(thrownIndex + "thrown a".length).trim { it <= ' ' }
                }
                // Split the message into bomb type and WC number
                val parts = unformattedMessage.split("on WC".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (parts.size == 2) {
                    bombType = parts[0].trim { it <= ' ' }
                    wcNumber = parts[1].trim { it <= ' ' }
                }
                val bombBellPrefix = Config.getConfigData()?.prefixBombbellToUse ?: ""
                if (bombType == "Combat XP Bomb" && Config.getConfigData()!!.combatXpBombEnabled) {
                    MessageFunctions.sendMessage(bombBellPrefix, "$bombBellPrefix A $bombType has been thrown on world $wcNumber")
                }
                if (bombType == "Profession XP Bomb" && Config.getConfigData()!!.professionXpBombEnabled) {
                    MessageFunctions.sendMessage(bombBellPrefix, "$bombBellPrefix A $bombType has been thrown on world $wcNumber")
                }
                if (bombType == "Profession Speed Bomb" && Config.getConfigData()!!.professionSpeedBombEnabled) {
                    MessageFunctions.sendMessage(bombBellPrefix, "$bombBellPrefix A $bombType has been thrown on world $wcNumber")
                }
                if (bombType == "Dungeon Bomb" && Config.getConfigData()!!.dungeonBombEnabled) {
                    MessageFunctions.sendMessage(bombBellPrefix, "$bombBellPrefix A $bombType has been thrown on world $wcNumber")
                }
                if (bombType == "Loot Bomb" && Config.getConfigData()!!.lootBombEnabled) {
                    MessageFunctions.sendMessage(bombBellPrefix, "$bombBellPrefix A $bombType has been thrown on world $wcNumber")
                }
            }
        }
    }

    companion object {
        var bombBellPrefix: String = "[Snow Bell]"
        private var instance: receiveMessage? = null
            get() {
                if (field == null) {
                    field = receiveMessage()
                }
                return field
            }

        @JvmStatic
        fun getReceiveMessageInstance(): receiveMessage {
            return instance ?: receiveMessage()
        }
    }
}
