package me.equixz.chatmood.functions

import com.mojang.brigadier.context.CommandContext
import me.equixz.chatmood.ChatModClient
import me.equixz.chatmood.structure.FileReader
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object MessageFunctions {
    fun sendMessage(prefix: String, message: String?) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player!!.networkHandler.sendChatMessage(prefix + message)
        }
    }

    fun changeBombBellPrefix(context: CommandContext<FabricClientCommandSource>, newPrefix: String) {
        val player = MinecraftClient.getInstance().player
        receiveMessage.bombBellPrefix = newPrefix
        if (newPrefix.length <= 15) {
            if (newPrefix.isEmpty()) {
                context.source.sendFeedback(
                    Text.literal("Please provide a non-empty message!").formatted(Formatting.RED)
                )
                return
            }
            player?.sendMessage(
                Text.literal("Bomb Bell prefix changed to: $newPrefix")
                    .formatted(Formatting.GREEN), false
            )
        } else {
            context.source.sendFeedback(
                Text.literal("Please provide a prefix that's under 15 characters!").formatted(
                    Formatting.RED
                )
            )
        }
    }

    fun changeMessage(newMessage: String) {
        ChatModClient.messageToSend = newMessage

        val player = MinecraftClient.getInstance().player

        if (newMessage.isEmpty() && player != null) {
            player.sendMessage(Text.literal("Please provide an existing file name!").formatted(Formatting.RED), false)
            return
        }
        player?.sendMessage(
            Text.literal("File output changed to: $newMessage")
                .formatted(Formatting.GREEN), false
        )
    }

    fun changeCooldown(context: CommandContext<FabricClientCommandSource>, newCooldown: String) {
        val cooldown = newCooldown.toInt()
        if (cooldown < 1250) {
            context.source.sendError(Text.of("Cooldown value must be 1250 or higher."))
            return
        }

        ChatModClient.delayIncrement = cooldown
        ChatModClient.initialDelay = cooldown

        val player = MinecraftClient.getInstance().player

        if (newCooldown.isEmpty()) {
            context.source.sendFeedback(Text.literal("Please provide a non-empty message!").formatted(Formatting.RED))
            return
        }
        player?.sendMessage(
            Text.literal("Message cooldown changed to: $newCooldown")
                .formatted(Formatting.GREEN), false
        )
    }

    fun changePrefix(newPrefix: String) {
        ChatModClient.prefixToUse = newPrefix

        val player = MinecraftClient.getInstance().player

        player?.sendMessage(
            Text.literal("Message chat changed to: $newPrefix")
                .formatted(Formatting.GREEN), false
        )
    }

    fun changePrefixBombbell(newPrefix: String) {
        ChatModClient.prefixBombbellToUse = newPrefix

        val player = MinecraftClient.getInstance().player

        player?.sendMessage(
            Text.literal("Bomb Bell chat changed to: $newPrefix")
                .formatted(Formatting.GREEN), false
        )
    }

    fun sendChatMessage() {
        // Get the file name based on the message
        val fileName: String =
            ChatModClient.messageToSend + ".txt" // Adjust the file name based on naming convention

        // Check if the file exists
        if (!FileReader.doesFileExist(fileName)) {
            val player = MinecraftClient.getInstance().player
            player?.sendMessage(Text.literal("The file doesn't exist.").formatted(Formatting.RED), false)
            return
        }
        // Read the lines from the file
        val lines = FileReader.readFiles(fileName)

        // Create a ScheduledExecutorService
        val executorService = Executors.newSingleThreadScheduledExecutor()

        // Schedule tasks with increasing delays
        var currentDelay: Long = ChatModClient.initialDelay.toLong()
        for (line in lines) {
            executorService.schedule({
                try {
                    sendMessage(ChatModClient.prefixToUse, line)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, currentDelay, TimeUnit.MILLISECONDS)
            currentDelay += ChatModClient.delayIncrement.toLong()
        }
        // Shutdown the executor service after all tasks are done
        executorService.shutdown()
    }
}
