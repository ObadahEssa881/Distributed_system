package coordinator;

import common.User;
import node.Node;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.*;

public class CoordinatorImpl extends UnicastRemoteObject implements CoordinatorInterface {
    private Coordinator auth;
    private Map<String, Node> departmentNodes;
    private int lastNodeIndex = 0;

    public CoordinatorImpl() throws RemoteException {
        auth = new Coordinator();

        departmentNodes = new HashMap<>();

        // Define ports for departments
        Map<String, Integer> ports = new HashMap<>();
        ports.put("development", 5001);
        ports.put("graphic", 5002);
        ports.put("qa", 5003);

        for (Map.Entry<String, Integer> entry : ports.entrySet()) {
            String dept = entry.getKey();
            int port = entry.getValue();
            Map<String, Integer> peers = new HashMap<>(ports);
            peers.remove(dept); // Don't sync to self
            departmentNodes.put(dept, new Node(dept, port, peers));
        }
    }

    public String login(String username, String password) throws RemoteException {
        return auth.login(username, password);
    }

    public boolean uploadFile(String token, String filename, byte[] data) throws RemoteException {
        User user = auth.getUserByToken(token);
        if (user != null) {
            Node node = departmentNodes.get(user.getDepartment());
            return node.getFileManager().saveFile(filename, data);
        }
        return false;
    }

    public byte[] downloadFile(String token, String filename) throws RemoteException {
        for (Node node : getShuffledNodes()) {
            byte[] data = node.getFileManager().readFile(filename);
            if (data != null)
                return data;
        }
        return null;
    }

    public List<String> listFiles(String token) throws RemoteException {
        Set<String> all = new HashSet<>();
        for (Node node : departmentNodes.values()) {
            all.addAll(node.getFileManager().listFiles());
        }
        return new ArrayList<>(all);
    }

    // Optional: expose sync trigger from client
    public void triggerSync() {
        departmentNodes.values().forEach(Node::syncFiles);
    }

    private List<Node> getShuffledNodes() {
        List<Node> nodes = new ArrayList<>(departmentNodes.values());
        Collections.rotate(nodes, ++lastNodeIndex);
        return nodes;
    }

    public boolean deleteFile(String token, String filename) throws RemoteException {
        User user = auth.getUserByToken(token);
        if (user != null) {
            return departmentNodes.get(user.getDepartment()).getFileManager().deleteFile(filename);
        }
        return false;
    }

    public boolean overwriteFile(String token, String filename, byte[] data) throws RemoteException {
        User user = auth.getUserByToken(token);
        if (user != null) {
            return departmentNodes.get(user.getDepartment()).getFileManager().saveFile(filename, data);
        }
        return false;
    }

}
