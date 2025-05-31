package coordinator;

import common.User;
import common.Role;

import java.util.*;

public class Coordinator {
    private Map<String, User> users = new HashMap<>();
    private Map<String, String> tokens = new HashMap<>();

    public Coordinator() {
        users.put("dev", new User("dev", "123", Role.EMPLOYEE, "development"));
        users.put("graphic", new User("graphic", "123", Role.EMPLOYEE, "graphic"));
        users.put("qa", new User("qa", "123", Role.EMPLOYEE, "qa"));
        users.put("admin", new User("admin", "admin", Role.MANAGER, "development"));
    }

    public String login(String username, String password) {
        if (users.containsKey(username) && users.get(username).getPassword().equals(password)) {
            String token = UUID.randomUUID().toString();
            tokens.put(token, username);
            return token;
        }
        return null;
    }

    public User getUserByToken(String token) {
        String username = tokens.get(token);
        return users.get(username);
    }
}


  
 