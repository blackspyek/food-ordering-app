package sample.test.service;

import java.util.List;

public class JwtTokenService {
    private static JwtTokenService instance;
    private String jwtToken;
    private List<String> userRoles;

    private JwtTokenService() {
    }

    public static JwtTokenService getInstance() {
        if (instance == null) {
            instance = new JwtTokenService();
        }
        return instance;
    }

    public void setJwtToken(String token) {
        this.jwtToken = token;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setUserRoles(List<String> roles) {
        this.userRoles = roles;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }

}
