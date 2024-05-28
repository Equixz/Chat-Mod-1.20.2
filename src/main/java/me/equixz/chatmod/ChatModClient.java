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
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import java.lang.reflect.Field;
import java.util.Objects;

@SuppressWarnings("CallToPrintStackTrace")
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
            if (screen != null) {
                ScreenKeyboardEvents.afterKeyPress(screen).register((parent, key, scancode, modifiers) -> {
                    if (nbtData.matchesKey(key, scancode)) {
                        Slot slotUnderMouse = getSlotUnderMouse();
                        if (slotUnderMouse != null && slotUnderMouse.hasStack()) {
                            ItemStack itemStack = slotUnderMouse.getStack();
                            if (itemStack.getCount() != 1) return;
                            NBTExtractor.getNBTData(itemStack);
                        }
                    }
                });
            }
        });
    }

    private Slot getSlotUnderMouse() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof HandledScreen<?> handledScreen) {
            double mouseX = client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();
            try {
                Field[] fields = HandledScreen.class.getDeclaredFields();
                Field xField = null, yField = null;
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.getType() == int.class) {
                        if (field.getName().equals("field_2776") || field.getName().equals("x")) {
                            xField = field;
                        } else if (field.getName().equals("field_2800") || field.getName().equals("y")) {
                            yField = field;
                        }
                    }
                }
                int left = (int) Objects.requireNonNull(xField).get(handledScreen);
                int top = (int) Objects.requireNonNull(yField).get(handledScreen);
                for (Slot slot : handledScreen.getScreenHandler().slots) {
                    int slotX = left + slot.x;
                    int slotY = top + slot.y;
                    int slotWidth = 16;
                    int slotHeight = 16;
                    if (mouseX >= slotX && mouseX < slotX + slotWidth && mouseY >= slotY && mouseY < slotY + slotHeight) {
                        return slot;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
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
}
