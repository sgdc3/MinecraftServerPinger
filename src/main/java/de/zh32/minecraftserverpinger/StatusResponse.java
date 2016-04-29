package de.zh32.minecraftserverpinger;

/**
 * Created by zh32 on 16.04.16.
 */
public interface StatusResponse {

    Integer getOnlinePlayers();

    Integer getMaxPlayers();

    String getDescription();
}
