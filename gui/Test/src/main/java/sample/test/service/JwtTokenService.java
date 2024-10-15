package sample.test.service;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtTokenService {
    private static JwtTokenService instance;
    private String jwtToken;

    public static JwtTokenService getInstance() {
        if (instance == null) {
            instance = new JwtTokenService();
        }
        return instance;
    }
}
