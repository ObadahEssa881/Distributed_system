package utils;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class FileSyncServer extends Thread {
    private int port;
    private String department;

    public FileSyncServer(int port, String department) {
        this.port = port;
        this.department = department;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Sync server for " + department + " listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handle(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            String filename = dis.readUTF();
            int length = dis.readInt();
            byte[] data = new byte[length];
            dis.readFully(data);

            Path path = Paths.get("data", department, filename);
            Files.write(path, data);
            System.out.println("[" + department + "] Synced file: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
