package me.equixz.chatmood

import me.equixz.chatmood.commands.Message
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
        var messageToSend: String = "Crazy"
        var prefixToUse: String = ""
        var prefixBombbellToUse: String = "/g "
        var initialDelay: Int = 1500
        var delayIncrement: Int = 1500

        private val sendMessageKey: KeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding("Send Group Messages", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.code, "ChatMod")
        )
    }
}
