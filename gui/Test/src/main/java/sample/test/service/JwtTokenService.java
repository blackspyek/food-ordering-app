package sample.test.service;

public class JwtTokenService {
    private static JwtTokenService instance;
    private String jwtToken;

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
}
