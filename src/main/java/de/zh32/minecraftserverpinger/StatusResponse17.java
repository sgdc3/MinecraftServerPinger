package de.zh32.minecraftserverpinger;

import com.google.gson.JsonArray;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zh32 <zh32 at zh32.de>
 */
@Data
public class StatusResponse17 implements StatusResponse {

    private Version version;
    private Players players;
    private String description;
    private String favicon;

    @Override
    public int getPlayerOnline() {
        return players.getOnline();
    }

    @Override
    public int getPlayerMax() {
        return players.getMax();
    }

    @Override
    public Map<String, String> getPlayerSample() {
        Map<String, String> sample = new HashMap<>();
        for (Player player : players.getSample()) {
            sample.put(player.getName(), player.getId());
        }
        return sample;
    }

    @Override
    public String getMotd() {
        return description;
    }

    @Override
    public JsonArray getMotdJson() {
        // 1.7 motd doesn't have JSON data
        return null;
    }

    @Override
    public String getIcon() {
        return favicon;
    }

    @Override
    public String getProtocolName() {
        return version.getName();
    }

    @Override
    public int getProtocolVersion() {
        return version.getProtocol();
    }

    @Getter
    private class Players {
        private int max;
        private int online;
        private List<Player> sample;
    }

    @Getter
    private class Player {
        private String name;
        private String id;
    }

    @Getter
    private class Version {
        private String name;
        private int protocol;
    }
}