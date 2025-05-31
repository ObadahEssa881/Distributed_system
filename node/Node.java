package node;

import utils.FileSyncServer;

import java.util.Map;

public class Node {
    private String department;
    private FileManager manager;
    private NodeSyncManager syncManager;

    public Node(String department, int port, Map<String, Integer> peerPorts) {
        this.department = department;
        this.manager = new FileManager(department);
        this.syncManager = new NodeSyncManager(department, port, peerPorts);

        // Start file sync server
        new FileSyncServer(port, department).start();
    }

    public FileManager getFileManager() {
        return manager;
    }

    public void syncFiles() {
        syncManager.syncToPeers();
    }
}
