package common;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private Role role;
    private String department;

    public User(String username, String password, Role role, String department) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.department = department;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getDepartment() { return department; }
}
