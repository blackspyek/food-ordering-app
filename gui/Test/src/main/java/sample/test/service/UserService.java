package sample.test.service;

import java.util.List;

public class UserService {
    private static UserService instance;
    private String username;
    private List<String> userRoles;

    private UserService() {
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUserRoles(List<String> roles) {
        this.userRoles = roles;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }
}

