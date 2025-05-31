package node;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {
    private Path dir;

    public FileManager(String department) {
        this.dir = Paths.get("data", department);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean saveFile(String filename, byte[] data) {
        try {
            Files.write(dir.resolve(filename), data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public synchronized byte[] readFile(String filename) {
        try {
            return Files.readAllBytes(dir.resolve(filename));
        } catch (IOException e) {
            return null;
        }
    }

    public List<String> listFiles() {
        File[] files = dir.toFile().listFiles();
        List<String> names = new ArrayList<>();
        if (files != null) {
            for (File f : files)
                names.add(f.getName());
        }
        return names;
    }

    public synchronized boolean deleteFile(String filename) {
        try {
            return Files.deleteIfExists(dir.resolve(filename));
        } catch (IOException e) {
            return false;
        }
    }
}
