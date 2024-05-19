package me.equixz.chatmood;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.URL;

public class ModUpdater {
    private static final String MOD_FILE_NAME = "chat-mod-x.x.x.jar";
    private static final String currentVersion;

    static {
        currentVersion = extractVersionFromFileName();
    }

    private static String extractVersionFromFileName() {
        int versionStartIndex = ModUpdater.MOD_FILE_NAME.lastIndexOf("x.");
        int versionEndIndex = ModUpdater.MOD_FILE_NAME.lastIndexOf(".jar");
        //noinspection DataFlowIssue
        return ModUpdater.MOD_FILE_NAME.substring(versionStartIndex + 2, versionEndIndex);
    }

    public void checkForUpdate() throws Exception {
        String latestVersion = getLatestVersionFromGithub();
        if (!currentVersion.equals(latestVersion)) {
            downloadNewVersion(latestVersion);
        }
    }

    private String getLatestVersionFromGithub() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.github.com/repos/Equixz/Chat-Mod-1.20.2/releases/latest").build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new RuntimeException("Unexpected code " + response);
            assert response.body() != null;
            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            return jsonObject.get("tag_name").getAsString();
        }
    }

    private void downloadNewVersion(String latestVersion) throws Exception {
        String downloadUrl = "https://github.com/Equixz/Chat-Mod-1.20.2/releases/download/" + latestVersion + "/chat-mod-" + latestVersion + ".jar";
        URL url = new URL(downloadUrl);
        InputStream in = url.openStream();
        OutputStream out = new FileOutputStream("mods/chat-mod-" + latestVersion + ".jar");

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        in.close();
        out.close();
    }

}
