package client;

import coordinator.CoordinatorInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class ClientGUI {
    private CoordinatorInterface stub;
    private String token = null;
    private JFrame loginFrame;
    private JFrame mainFrame;

    public static void main(String[] args) {
        new ClientGUI().start();
    }

    public void start() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            stub = (CoordinatorInterface) registry.lookup("Coordinator");
            showLoginWindow();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to Coordinator.");
            e.printStackTrace();
        }
    }

    private void showLoginWindow() {
        loginFrame = new JFrame("Login");
        loginFrame.setSize(300, 150);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passText = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        loginBtn.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());
            try {
                token = stub.login(username, password);
                if (token != null) {
                    loginFrame.dispose();
                    showMainWindow(username);
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid credentials.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(loginFrame, "Login failed.");
                ex.printStackTrace();
            }
        });

        panel.add(userLabel);
        panel.add(userText);
        panel.add(passLabel);
        panel.add(passText);
        panel.add(new JLabel());
        panel.add(loginBtn);

        loginFrame.add(panel);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private void showMainWindow(String username) {
        mainFrame = new JFrame("File Dashboard - Logged in as " + username);
        mainFrame.setSize(500, 400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new GridLayout(5, 1));

        JButton uploadBtn = new JButton("Upload File");
        JButton listBtn = new JButton("List Files");
        JButton downloadBtn = new JButton("Download File");
        JTextArea outputArea = new JTextArea();
        JButton deleteBtn = new JButton("Delete File");
        JButton overwriteBtn = new JButton("Overwrite File");

        uploadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    fis.close();
                    byte[] data = baos.toByteArray();

                    boolean uploaded = stub.uploadFile(token, file.getName(), data);
                    outputArea.setText(uploaded ? "Upload successful." : "Upload failed.");
                } catch (Exception ex) {
                    outputArea.setText("Error uploading file.");
                    ex.printStackTrace();
                }
            }
        });

        listBtn.addActionListener(e -> {
            try {
                List<String> files = stub.listFiles(token);
                outputArea.setText("Files:\n" + String.join("\n", files));
            } catch (Exception ex) {
                outputArea.setText("Failed to list files.");
                ex.printStackTrace();
            }
        });

        downloadBtn.addActionListener(e -> {
            String filename = JOptionPane.showInputDialog("Enter filename to download:");
            try {
                byte[] data = stub.downloadFile(token, filename);
                if (data != null) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setSelectedFile(new File(filename));
                    int result = chooser.showSaveDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File saveFile = chooser.getSelectedFile();
                        FileOutputStream fos = new FileOutputStream(saveFile);
                        fos.write(data);
                        fos.close();
                        outputArea.setText("Downloaded and saved to: " + saveFile.getAbsolutePath());
                    }
                } else {
                    outputArea.setText("File not found.");
                }
            } catch (Exception ex) {
                outputArea.setText("Error downloading file.");
                ex.printStackTrace();
            }
        });
        deleteBtn.addActionListener(e -> {
            String filename = JOptionPane.showInputDialog("Enter filename to delete:");
            try {
                boolean ok = stub.deleteFile(token, filename);
                outputArea.setText(ok ? "Deleted successfully." : "Delete failed.");
            } catch (Exception ex) {
                outputArea.setText("Error deleting file.");
            }
        });

        overwriteBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    FileInputStream fis = new FileInputStream(file);
                    byte[] buf = new byte[1024];
                    int bytes;
                    while ((bytes = fis.read(buf)) != -1)
                        baos.write(buf, 0, bytes);
                    fis.close();

                    boolean ok = stub.overwriteFile(token, file.getName(), baos.toByteArray());
                    outputArea.setText(ok ? "Overwrite successful." : "Failed to overwrite.");
                } catch (Exception ex) {
                    outputArea.setText("Error overwriting file.");
                }
            }
        });

        mainFrame.add(uploadBtn);
        mainFrame.add(listBtn);
        mainFrame.add(downloadBtn);
        mainFrame.add(new JScrollPane(outputArea));
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        mainFrame.add(overwriteBtn);
        mainFrame.add(deleteBtn);

    }
}
