package me.equixz.chatmod.functions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.equixz.chatmod.ChatMod;
import me.equixz.chatmod.record.KeyAndValue;
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
import static me.equixz.chatmod.functions.JsonObjectMap.createJsonObjectMap;
import static me.equixz.chatmod.functions.StringMap.createStringMap;
import static net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON;

@SuppressWarnings("CallToPrintStackTrace")
public class NBTExtractor {
    private static Map<String, String> identificationMap;
    private static Map<String, JsonObject> weightMap;
    private static Map<String, JsonObject> itemMap;

    static {
        try (InputStream inputStream = NBTExtractor.class.getClassLoader().getResourceAsStream("ids.json")) {
            if (inputStream != null) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);

                identificationMap = createStringMap(jsonObject);
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

                weightMap = createJsonObjectMap(jsonObject);
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

                itemMap = createJsonObjectMap(jsonObject);
            } else {
                ChatMod.LOGGER.error("mythics.json not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String stripColorCodes(String input) {
        return input.replaceAll("§[0-9A-FK-ORa-fk-or]", "");
    }

    public static List<String> processItemStack(ItemStack itemStack) {
        List<String> loreEntries = new ArrayList<>();
        NbtCompound nbtTagCompound = itemStack.getNbt();
        if (nbtTagCompound != null && nbtTagCompound.contains("display")) {
            NbtCompound displayTag = nbtTagCompound.getCompound("display");
            if (displayTag.contains("Lore")) {
                NbtList loreList = displayTag.getList("Lore", 8);
                for (int i = 0; i < loreList.size(); i++) {
                    String loreItem = loreList.getString(i);
                    JsonObject loreObject = GSON.fromJson(loreItem, JsonObject.class);
                    if (loreObject.has("extra")) {
                        JsonArray extraArray = loreObject.getAsJsonArray("extra");
                        StringBuilder result = new StringBuilder();
                        for (JsonElement element : extraArray) {
                            JsonObject extraObject = element.getAsJsonObject();
                            result.append(extraObject.get("text").getAsString());
                        }
                        loreEntries.add(result.toString());
                    } else if (loreObject.has("text")) {
                        loreEntries.add(loreObject.get("text").getAsString());
                    }
                }
            }
        }
        return loreEntries;
    }

    public static void getNBTData(ItemStack itemStack) {
        ClientPlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        List<KeyAndValue> keyAndValueList = new ArrayList<>();
        List<String> loreList = processItemStack(itemStack);
        String itemName = stripColorCodes(itemStack.getName().getString()).replaceAll("À", "");
		for (String loreEntry : loreList) {
			if (loreEntry.startsWith("+") || loreEntry.startsWith("-")) {
				String value = stripColorCodes(loreEntry).replaceAll(".*?(-?\\d+).*", "$1");
				String filteredLoreEntry = stripColorCodes(loreEntry).replace("**", "").replace("*", "");
				Pattern pattern = Pattern.compile("([+-]?\\d+)\\s*(.*)");
				Matcher matcher = pattern.matcher(filteredLoreEntry);
				if (matcher.matches()) {
					String text = matcher.group(2);
					if (identificationMap.containsKey(text)) {
						String jsonKey = identificationMap.get(text);
						if (jsonKey.contains("rawIntelligence")
								|| jsonKey.contains("rawStrength") || jsonKey.contains("rawDexterity")
								|| jsonKey.contains("rawAgility") || jsonKey.contains("rawDefence")) continue;
						if (itemMap.containsKey(itemName)) {
							JsonObject itemObject = new Gson().fromJson(itemMap.get(itemName), JsonObject.class);
							JsonObject identifications = itemObject.getAsJsonObject("identifications");
							if (identifications.has(jsonKey)) {
								JsonObject jsonValue = identifications.getAsJsonObject(jsonKey);
								int min = jsonValue.get("min").getAsInt();
								int max = jsonValue.get("max").getAsInt();
								double value2 = (((Double.parseDouble(value) - min) * 100) / (max - min));
								keyAndValueList.add(new KeyAndValue(jsonKey, value2));
							}
						} else {
							return;
						}
					} else {
						player.sendMessage(Text.literal(text + " (Unknown identification)").formatted(Formatting.RED), false);
					}
				}
			}
		}
		List<Double> percentageList = new ArrayList<>();
		if (weightMap.containsKey(itemName)) {
			JsonObject weightObject = weightMap.get(itemName);
			for (Map.Entry<String, JsonElement> entry : weightObject.entrySet()) {
				double percentage = getPercentage(entry, keyAndValueList);
				percentageList.add(percentage);
			}
		}
		double sum = percentageList.stream().mapToDouble(Double::doubleValue).sum();
		player.sendMessage(Text.literal("The weight of this " + itemName + " is: " + String.format("%.2f", sum)).formatted(Formatting.GREEN), false);
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