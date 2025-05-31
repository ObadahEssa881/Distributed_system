package node;

import utils.FileSyncClient;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class NodeSyncManager {
    private String department;
    private int port;
    private Map<String, Integer> peerPorts;
    private final Path syncLogPath;

    public NodeSyncManager(String department, int port, Map<String, Integer> peerPorts) {
        this.department = department;
        this.port = port;
        this.peerPorts = peerPorts;
        this.syncLogPath = Paths.get("sync_" + department + ".log");

        startScheduledSync(); // Automatically starts repeating task
    }

    private void startScheduledSync() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::syncToPeers, 30, 30, TimeUnit.SECONDS); // Every 30s
        log("Scheduled sync every 30 seconds started for department: " + department);
    }

    public void syncToPeers() {
        File dir = Paths.get("data", department).toFile();
        File[] files = dir.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            try {
                byte[] data = Files.readAllBytes(file.toPath());
                for (Map.Entry<String, Integer> peer : peerPorts.entrySet()) {
                    log("Sending file '" + file.getName() + "' to " + peer.getKey());
                    FileSyncClient.sendFile("localhost", peer.getValue(), file.getName(), data);
                }
            } catch (IOException e) {
                log("Error syncing file: " + e.getMessage());
            }
        }
    }

    private void log(String message) {
        try {
            Files.write(syncLogPath, (new Date() + " - " + message + "\n").getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException ignored) {
        }
        System.out.println("[" + department + "] " + message);
    }
}
