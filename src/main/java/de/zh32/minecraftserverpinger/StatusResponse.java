package de.zh32.minecraftserverpinger;

import com.google.gson.JsonArray;

import java.util.Map;

/**
 * Created by zh32 on 16.04.16.
 */
public interface StatusResponse {
    int getPlayerOnline();

    int getPlayerMax();

    Map<String, String> getPlayerSample(); // Name, ID

    String getMotd();

    JsonArray getMotdJson();

    String getIcon();

    String getProtocolName();

    int getProtocolVersion();
}
