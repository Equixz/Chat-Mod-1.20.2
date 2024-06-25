package me.equixz.chatmod.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.equixz.chatmod.ChatMod.LOGGER;

public class extractWCNumber {
    public static void extractWC(MinecraftClient client) {
        if (client != null && client.player != null && client.world != null) {
            Collection<PlayerListEntry> playerListEntries = Objects.requireNonNull(client.getNetworkHandler()).getPlayerList();
            for (PlayerListEntry playerListEntry : playerListEntries) {
                String playerUuid = playerListEntry.getProfile().getId().toString();
                if (playerUuid.equals("16ff7452-714f-3752-b3cd-c3cb2068f6af")) {
                    String displayName = playerListEntry.getDisplayName() != null ? playerListEntry.getDisplayName().getString() : "N/A";
                    Pattern pattern = Pattern.compile("\\[WC\\d+]");
                    Matcher matcher = pattern.matcher(displayName);
                    String filteredDisplayName = "N/A";
                    if (matcher.find()) {
                        filteredDisplayName = matcher.group();
                        filteredDisplayName = filteredDisplayName.replace("[WC", "").replace("]", "");
                    }
                    LOGGER.info("Current World: {}", filteredDisplayName);
                    return;
                }
            }
        }
    }
}
