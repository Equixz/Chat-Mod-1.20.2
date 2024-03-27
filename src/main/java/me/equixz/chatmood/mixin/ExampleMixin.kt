package me.equixz.chatmood.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ExampleMixin {
    @Inject(at = @At("HEAD"), method = "onChatMessage")
    private void onChatMessage(@NotNull ChatMessageS2CPacket packet, CallbackInfo ci) {
        // Access the chat message from the packet
        // String message = String.valueOf(packet.getClass());

        // Your custom logic here
        // System.out.println("Received packet: " + message);
    }
}