package client;

import coordinator.CoordinatorInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        CoordinatorInterface stub = (CoordinatorInterface) registry.lookup("Coordinator");

        Scanner input = new Scanner(System.in);
        System.out.print("Username: ");
        String user = input.nextLine();
        System.out.print("Password: ");
        String pass = input.nextLine();

        String token = stub.login(user, pass);
        if (token == null) {
            System.out.println("Login failed.");
            return;
        }

        System.out.println("Login successful. Token: " + token);

        System.out.println("Choose action: 1) Upload 2) List 3) Download");
        int choice = input.nextInt();
        input.nextLine(); // consume newline

        if (choice == 1) {
            System.out.print("Filename: ");
            String name = input.nextLine();
            System.out.print("Content: ");
            String content = input.nextLine();
            boolean ok = stub.uploadFile(token, name, content.getBytes());
            System.out.println(ok ? "Upload OK" : "Upload failed");
        } else if (choice == 2) {
            System.out.println("Files: " + stub.listFiles(token));
        } else if (choice == 3) {
            System.out.print("Filename to download: ");
            String name = input.nextLine();
            byte[] file = stub.downloadFile(token, name);
            if (file != null)
                System.out.println("Downloaded: " + new String(file));
            else
                System.out.println("File not found.");
        }
    }
}
