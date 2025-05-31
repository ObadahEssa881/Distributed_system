package coordinator;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CoordinatorInterface extends Remote {
    String login(String username, String password) throws RemoteException;

    boolean uploadFile(String token, String filename, byte[] data) throws RemoteException;

    byte[] downloadFile(String token, String filename) throws RemoteException;

    List<String> listFiles(String token) throws RemoteException;

    boolean deleteFile(String token, String filename) throws RemoteException;

    boolean overwriteFile(String token, String filename, byte[] data) throws RemoteException;

}
