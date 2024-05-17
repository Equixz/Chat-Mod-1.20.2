package me.equixz.chatmood.mixin;

import me.equixz.chatmood.functions.ReceiveMessage;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class OnChatMixin {
    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        String message = packet.content().getString();
        ReceiveMessage.getReceiveMessageInstance().receiveMessages(Text.of(message));
    }
}
