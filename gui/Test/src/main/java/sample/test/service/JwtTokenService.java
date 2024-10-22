package sample.test.service;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

/**
 * Singleton service class responsible for managing the JWT (JSON Web Token) used for authenticating API requests.
 * <p>
 * This class stores the JWT token and provides access to it throughout the application. It follows the singleton
 * design pattern to ensure only one instance of the service is created. The class also restricts object creation
 * by making the constructor private using Lombok's @NoArgsConstructor with access level set to PRIVATE.
 * <p>
 * Key responsibilities include:
 * - Storing the JWT token.
 * - Providing a global point of access to the JWT token through the getInstance() method.
 */

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtTokenService {
    private static JwtTokenService instance;
    private String jwtToken;

    /**
     * Method to retrieve the singleton instance of the JwtTokenService class.
     *
     * @return The singleton instance of the JwtTokenService class.
     */
    public static JwtTokenService getInstance() {
        if (instance == null) {
            instance = new JwtTokenService();
        }
        return instance;
    }
}
