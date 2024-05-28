package me.equixz.chatmod;

import me.equixz.chatmod.commands.Message;
import me.equixz.chatmod.functions.MessageFunctions;
import me.equixz.chatmod.functions.NBTExtractor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import java.lang.reflect.Field;
import java.util.Objects;

public class ChatModClient implements ClientModInitializer {
    private static final KeyBinding sendMessageKey = registerKeyBinding("Send Group Messages");
    private static final KeyBinding sendLastBombbell = registerKeyBinding("Send Last Bombbell");
    private static final KeyBinding switchWorldsLastBombbell = registerKeyBinding("Switch Worlds to latest Bombbell");
    private static final KeyBinding nbtData = registerKeyBinding("Get NBT Data");

    @Override
    public void onInitializeClient() {
        Message.registerBaseCommand();
        ClientTickEvents.END_CLIENT_TICK.register(client -> handleClientTick());
        ScreenEvents.AFTER_INIT.register((client, screen, scaleWidth, scaleHeight) -> {
            // This works 100% fine
            if (screen != null) {
                ScreenKeyboardEvents.afterKeyPress(screen).register((parent, key, scancode, modifiers) -> {
                    if (nbtData.matchesKey(key, scancode)) {
                        Slot slot = getSlotUnderMouse((InventoryScreen) client.currentScreen, client.mouse.getX(), client.mouse.getY());
                        if (slot != null && slot.hasStack()) {
                            ItemStack itemStack = slot.getStack();
                            NBTExtractor.getNBTData(itemStack);
                        }
                    }
                });
            }
        });
    }

    private static KeyBinding registerKeyBinding(String description) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(description, InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "ChatMod"));
    }

    private static void handleClientTick() {
        if (sendMessageKey.wasPressed()) {
            MessageFunctions.sendChatMessage();
        }
        if (sendLastBombbell.wasPressed()) {
            MessageFunctions.sendLastBombbell();
        }
        if (switchWorldsLastBombbell.wasPressed()) {
            MessageFunctions.switchToLatestBombbell();
        }
        if (nbtData.wasPressed()) {
            MinecraftClient client = MinecraftClient.getInstance();
            NBTExtractor.getNBTData(Objects.requireNonNull(client.player).getMainHandStack());
        }
    }

    private static Slot getSlotUnderMouse(InventoryScreen inventoryScreen, double mouseX, double mouseY) {
        for (Slot slot : inventoryScreen.getScreenHandler().slots) {
            if (isMouseOverSlot(slot, inventoryScreen, mouseX, mouseY)) {
                return slot;
            }
        }
        return null;
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    private static boolean isMouseOverSlot(Slot slot, InventoryScreen inventoryScreen, double mouseX, double mouseY) {
        try {
            // Use reflection to access the protected fields x and y in Slot
            Field slotXField = Slot.class.getDeclaredField("x");
            Field slotYField = Slot.class.getDeclaredField("y");
            slotXField.setAccessible(true);
            slotYField.setAccessible(true);
            int slotX = slotXField.getInt(slot);
            int slotY = slotYField.getInt(slot);
            // Use reflection to access the protected fields guiLeft and guiTop in HandledScreen
            Field guiLeftField = InventoryScreen.class.getDeclaredField("x");
            Field guiTopField = InventoryScreen.class.getDeclaredField("y");
            guiLeftField.setAccessible(true);
            guiTopField.setAccessible(true);
            int guiLeft = guiLeftField.getInt(inventoryScreen);
            int guiTop = guiTopField.getInt(inventoryScreen);
            // Adjust mouse coordinates to be relative to the GUI
            mouseX -= guiLeft;
            mouseY -= guiTop;
            // Check if the mouse is within the slot bounds
            return mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return false;
        }
    }
}
