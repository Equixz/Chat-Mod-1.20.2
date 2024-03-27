package me.equixz.chatmood.mixin

import me.equixz.chatmood.functions.receiveMessage
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ClientPlayNetworkHandler::class)
class onChatMixin {
    @Inject(
        method = ["onGameMessage(Lnet/minecraft/network/packet/s2c/play/GameMessageS2CPacket;)V"],
        at = [At("HEAD")]
    )
    private fun onGameMessage(packet: GameMessageS2CPacket, ci: CallbackInfo) {
        val message = packet.content()
        receiveMessage.getReceiveMessageInstance().receiveMessages(message)
    }
}
