package me.equixz.chatmod.functions;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.equixz.chatmod.ChatMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NBTExtractor {

    private static Map<String, String> identificationMap;
    private static Map<String, JsonObject > weightMap;
    private static Map<String, JsonObject> itemMap;

    static {
        try (InputStream inputStream = NBTExtractor.class.getClassLoader().getResourceAsStream("ids.json")) {
            if (inputStream != null) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);

                identificationMap = createIdentificationMap(jsonObject);
                createReverseIdentificationMap(identificationMap);
            } else {
                ChatMod.LOGGER.error("ids.json not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (InputStream inputStream = NBTExtractor.class.getClassLoader().getResourceAsStream("mythic_weights.json")) {
            if (inputStream != null) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);

                weightMap = createMythicMap(jsonObject);
            } else {
                ChatMod.LOGGER.error("mythic_weights.json not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (InputStream inputStream = NBTExtractor.class.getClassLoader().getResourceAsStream("mythics.json")) {
            if (inputStream != null) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);

                itemMap = createMythicMap(jsonObject);
            } else {
                ChatMod.LOGGER.error("mythics.json not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, JsonObject> createMythicMap(JsonObject jsonObject) {
        Map<String, JsonObject> map = new HashMap<>();
        jsonObject.entrySet().forEach(entry -> map.put(entry.getKey(), entry.getValue().getAsJsonObject()));
        return map;
    }

    private static Map<String, String> createIdentificationMap(JsonObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        jsonObject.entrySet().forEach(entry -> {
            if (entry.getValue().isJsonArray()) {
                entry.getValue().getAsJsonArray().forEach(element -> map.put(element.getAsString(), entry.getKey()));
            } else {
                map.put(entry.getValue().getAsString(), entry.getKey());
            }
        });
        return map;
    }

    private static void createReverseIdentificationMap(Map<String, String> identificationMap) {
        //noinspection MismatchedQueryAndUpdateOfCollection
        Map<String, String> reverseMap = new HashMap<>();
        identificationMap.forEach((key, value) -> reverseMap.put(value, key));
    }

    private static String stripColorCodes(String input) {
        return input.replaceAll("§[0-9A-FK-ORa-fk-or]", "");
    }

    public static void getNBTData() {
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        List<KeyAndValue> keyAndValueList = new ArrayList<>();
        MinecraftClient mc = MinecraftClient.getInstance();
        ItemStack itemStack = mc.player.getMainHandStack();
        if (itemStack != null && itemStack.hasNbt()) {
            NbtCompound nbt = itemStack.getNbt();
            if (nbt != null && nbt.contains("display")) {
                NbtCompound displayTag = nbt.getCompound("display");
                if (displayTag.contains("VV|Protocol1_14To1_13_2|Lore")) {
                    NbtList loreList = displayTag.getList("VV|Protocol1_14To1_13_2|Lore", 8);
                    String itemName = itemStack.getName().getString();
                    String itemName2 = stripColorCodes(itemName);
                    for (int i = 0; i < loreList.size(); i++) {
                        String loreEntry = loreList.getString(i);
                        if (loreEntry.startsWith("§7§a+") || loreEntry.startsWith("§7§a-") || loreEntry.startsWith("§7§c+") || loreEntry.startsWith("§7§c-")) {
                            String strippedLoreEntry = stripColorCodes(loreEntry);
                            String value = strippedLoreEntry.replaceAll(".*?(-?\\d+).*", "$1");
                            String Entry = stripColorCodes(loreEntry);
                            String filteredLoreEntry = Entry.replace("**", "").replace("*", "");
                            Pattern pattern = Pattern.compile("([+-]?\\d+)\\s*(.*)");
                            Matcher matcher = pattern.matcher(filteredLoreEntry);
                            if (matcher.matches()) {
                                String text = matcher.group(2);
                                if (identificationMap.containsKey(text)) {
                                    String jsonKey = identificationMap.get(text);
                                    if (jsonKey.contains("rawIntelligence") || jsonKey.contains("rawStrength") || jsonKey.contains("rawDexterity") || jsonKey.contains("rawAgility") || jsonKey.contains("rawDefence")) {
                                        continue;
                                    }
                                    if (itemMap.containsKey(itemName2)) {
                                        JsonObject itemObject = new Gson().fromJson(itemMap.get(itemName2), JsonObject.class);
                                        JsonObject identifications = itemObject.getAsJsonObject("identifications");
                                        if (identifications.has(jsonKey)) {
                                            JsonObject jsonValue = identifications.getAsJsonObject(jsonKey);
                                            int min = jsonValue.get("min").getAsInt();
                                            int max = jsonValue.get("max").getAsInt();
                                            double value2 = (((Double.parseDouble(value) - min) * 100) / (max - min));
                                            keyAndValueList.add(new KeyAndValue(jsonKey, value2));
                                        }
                                    }
                                } else {
                                    player.sendMessage(Text.literal(text + " (Unknown identification)").formatted(Formatting.RED), false);
                                }
                            }
                        }
                    }
                }
            }
            String itemName = itemStack.getName().getString();
            String itemName2 = stripColorCodes(itemName);
            List<Double> percentageList = new ArrayList<>();
            if (weightMap.containsKey(itemName2)) {
                JsonObject weightObject = weightMap.get(itemName2);
                for (Map.Entry<String, JsonElement> entry : weightObject.entrySet()) {
                    double percentage = getPercentage(entry, keyAndValueList);
                    percentageList.add(percentage);

                }
            }
            double sum = percentageList.stream().mapToDouble(Double::doubleValue).sum();
            String formattedSum = String.format("%.2f", sum);
            player.sendMessage(Text.literal("The weight of this " + itemName2 + " is: " + formattedSum).formatted(Formatting.GREEN), false);
        } else {
            player.sendMessage(Text.literal("This item has no NBT Data.").formatted(Formatting.RED), false);
        }
    }

    private static double getPercentage(Map.Entry<String, JsonElement> entry, List<KeyAndValue> keyAndValueList) {
        String attributeName = entry.getKey();
        double weight = entry.getValue().getAsDouble();
        double adjustedValue2 = 0.0;
        for (KeyAndValue keyAndValue : keyAndValueList) {
            if (keyAndValue.jsonKey().equals(attributeName)) {
                adjustedValue2 = keyAndValue.value2();
                break;
            }
        }
        return (weight * adjustedValue2) / 100;
    }
}