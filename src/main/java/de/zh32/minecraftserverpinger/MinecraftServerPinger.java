package de.zh32.minecraftserverpinger;
 
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * MC Ping API
 *
 * @author zh32 <zh32 at zh32.de>
 * @author sgdc3 <sgdc3.mail at gmail.com>
 */
public class MinecraftServerPinger {
    
    private static final int DEFAULT_TIMEOUT = 3000;
    
    private static final Gson gson = new Gson();
    
    @Getter @Setter
    private InetSocketAddress host;
    @Getter @Setter
    private int timeout;
    
    public MinecraftServerPinger(InetSocketAddress host, int timeout) {
        this.host = host;
        this.timeout = timeout;
    }
    
    public MinecraftServerPinger(InetSocketAddress host) {
        this(host, DEFAULT_TIMEOUT);
    }
    
    public MinecraftServerPinger() {
        this(null);
    }
    
    public StatusResponse fetchData() throws IOException, InvalidResponseException {
        @Cleanup
        Socket socket = new Socket();
        socket.setSoTimeout(timeout);
        socket.connect(host, timeout);
        
        @Cleanup
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        @Cleanup 
        DataInputStream in = new DataInputStream(socket.getInputStream());
        
        // Send handshake
        sendHandshake(out, host);
        
        // Returns the data
        return doQuery(out, in, host);
    }
    
    private void sendHandshake(DataOutputStream out, InetSocketAddress host) throws IOException {
        @Cleanup ByteArrayOutputStream bs = new ByteArrayOutputStream();
        @Cleanup DataOutputStream handshake = new DataOutputStream(bs);
        
        handshake.write(0x00); //packet id
        writeVarInt(handshake, 47); //protocol version
        writeString(handshake, host.getHostName());
        handshake.writeShort(host.getPort());
        writeVarInt(handshake, 1); //target state 1
        
        sendPacket(out, bs.toByteArray());
    }

    private StatusResponse doQuery(DataOutputStream out, DataInputStream in, InetSocketAddress host) throws IOException, InvalidResponseException {
        sendPacket(out, new byte[]{0x00});
        readVarInt(in); // Ignore size
        int packetId = readVarInt(in);
        if (packetId != 0x00) {
            throw new IOException("Invalid packetId");
        }
        int stringLength = readVarInt(in);
        if (stringLength < 1) {
            throw new IOException("Invalid string length.");
        }
        byte[] responseData = new byte[stringLength];
        in.readFully(responseData);
        String jsonString = new String(responseData, Charset.forName("utf-8"));
        try {
            return gson.fromJson(jsonString, StatusResponse19.class);
        } catch (JsonSyntaxException ignored) {}
        try {
            return gson.fromJson(jsonString, StatusResponse17.class);
        } catch (JsonSyntaxException ignored) {}
        throw new InvalidResponseException();
    }

    private int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
            if ((k & 0x80) != 128) {
                break;
            }
        }
        return i;
    }

    private void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.write(paramInt);
                return;
            }
            out.write(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    private void writeString(DataOutputStream out, String string) throws IOException {
        writeVarInt(out, string.length());
        out.write(string.getBytes(Charset.forName("utf-8")));
    }

    private void sendPacket(DataOutputStream out, byte[] data) throws IOException {
        writeVarInt(out, data.length);
        out.write(data);
    }
}
