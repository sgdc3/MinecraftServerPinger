package de.zh32.minecraftserverpinger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

public class Example {
    public static void main(String[] args) {
        MinecraftServerPinger pinger = new MinecraftServerPinger(new InetSocketAddress("mc.feargames.it", 25565));
        StatusResponse response;
        try {
            response = pinger.fetchData();
        } catch (SocketTimeoutException te) {
            System.out.println("Timeout!");
            return;
        } catch(InvalidResponseException | IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        System.out.println("Request: " + pinger.getHost().toString());
        System.out.println("--------------------------------");
        System.out.println("Motd: ");
        System.out.println(response.getDescription());
        System.out.println();
        System.out.println("Online players: " + response.getOnlinePlayers());
        System.out.println("Max players: " + response.getMaxPlayers());
        System.out.println();
    }
}
