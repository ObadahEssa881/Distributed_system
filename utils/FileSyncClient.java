package utils;

import java.io.*;
import java.net.*;

public class FileSyncClient {
    public static void sendFile(String host, int port, String filename, byte[] data) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000); // 2s timeout
            try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                dos.writeUTF(filename);
                dos.writeInt(data.length);
                dos.write(data);
            }
        } catch (IOException e) {
            System.err
                    .println("⚠️  [Sync Warning] Could not connect to " + host + ":" + port + " (node may be offline)");
        }
    }
}
