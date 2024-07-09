package me.equixz.chatmod.functions;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectMap {
	public static Map<String, JsonObject> createJsonObjectMap(JsonObject jsonObject) {
		Map<String, JsonObject> map = new HashMap<>();
		jsonObject.entrySet().forEach(entry -> map.put(entry.getKey(), entry.getValue().getAsJsonObject()));
		return map;
	}
}
