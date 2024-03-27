package me.equixz.chatmood

import me.equixz.chatmood.commands.Message
import me.equixz.chatmood.config.Config
import me.equixz.chatmood.functions.MessageFunctions
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

class ChatModClient : ClientModInitializer {
    override fun onInitializeClient() {
        Message.registerBaseCommand()
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            this.onClientTick()
        })
    }

    private fun onClientTick() {
        if (sendMessageKey.wasPressed()) {
            MessageFunctions.sendChatMessage()
        }
    }

    companion object {
        private var messageToSend: String = Config.getConfigData()?.messageToSend ?: "Crazy"
        private var prefixToUse: String = Config.getConfigData()?.prefixToUse ?: ""
        private var prefixBombbellToUse: String = Config.getConfigData()?.prefixBombbellToUse ?: "/g "
        private var initialDelay: Int = Config.getConfigData()?.initialDelay ?: 1250

        private val sendMessageKey: KeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding("Send Group Messages", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.code, "ChatMod")
        )
        init {
            updateConfigValues()
            Config.saveConfigData()
        }
        private fun updateConfigValues() {
            val configData = Config.getConfigData()
            messageToSend = configData?.messageToSend ?: "Crazy"
            prefixToUse = configData?.prefixToUse ?: ""
            prefixBombbellToUse = configData?.prefixBombbellToUse ?: "/g "
            configData?.initialDelay = initialDelay.coerceAtLeast(1250)
        }
    }
}