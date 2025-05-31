package utils;

import java.io.*;
import java.net.*;

public class FileSyncSocket {
    public static void syncFile(String host, int port, String filename, byte[] data) throws IOException {
        try (Socket socket = new Socket(host, port);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            out.writeUTF(filename);
            out.writeInt(data.length);
            out.write(data);
        }
    }
}
