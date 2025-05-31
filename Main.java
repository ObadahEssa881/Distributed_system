import coordinator.CoordinatorImpl;
import coordinator.CoordinatorInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
            CoordinatorInterface impl = new CoordinatorImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("Coordinator", impl);
            System.out.println("Coordinator is running...");
            Thread.sleep(10000); // Wait 10 seconds

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
