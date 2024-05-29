package me.equixz.chatmod.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

import java.lang.reflect.Field;
import java.util.Objects;

public class getSlotUnderMouse {
    @SuppressWarnings("CallToPrintStackTrace")
    public static Slot getSlot() {
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

}
