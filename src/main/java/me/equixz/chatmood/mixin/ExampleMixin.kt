package me.equixz.chatmood.mixin

import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ClientPlayNetworkHandler::class)
class ExampleMixin {
    @Inject(at = [At("HEAD")], method = ["onChatMessage"])
    private fun onChatMessage(packet: ChatMessageS2CPacket, ci: CallbackInfo) {
        // Access the chat message from the packet
        // String message = String.valueOf(packet.getClass());

        // Your custom logic here
        // System.out.println("Received packet: " + message);
    }
}