package me.equixz.chatmod.functions;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class StringMap {
	public static Map<String, String> createStringMap(JsonObject jsonObject) {
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
}
