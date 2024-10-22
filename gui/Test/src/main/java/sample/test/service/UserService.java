package sample.test.service;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sample.test.dto.UpdateUserDto;
import sample.test.model.User;
import sample.test.utils.HttpUtils;
import sample.test.helpers.UsersResponse;
import sample.test.helpers.UserResponse;

import java.net.http.HttpResponse;
import java.util.List;

/**
 * Singleton service class responsible for handling operations related to the user management in the application.
 * This class provides functionality for fetching users, updating user details, and deleting users by interacting
 * with the backend API. It also stores the current logged-in user's username and roles.
 * <p>
 * The class integrates with external services such as the JwtTokenService for authorization tokens
 * and HttpUtils for sending HTTP requests to the backend API. JSON responses are parsed using Gson.
 * <p>
 * It includes methods to:
 * - Get a list of users.
 * - Fetch user details by user ID.
 * - Update user information.
 * - Delete a user by user ID.
 * <p>
 * It also follows the singleton design pattern, ensuring only one instance of UserService exists.
 */

@Setter
@Getter
@NoArgsConstructor
public class UserService {
    private static UserService instance;
    private String username;
    private List<String> userRoles;

    /**
     * Private constructor to prevent instantiation of the class from outside.
     */
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Method to reset the instance of the UserService class.
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Method to fetch the list of users from the backend API.
     *
     * @return List of User objects.
     */
    public List<User> getUsers() {
        try {
            String url = "http://localhost:8080/users/";
            String token = JwtTokenService.getInstance().getJwtToken();

            HttpResponse<String> response = HttpUtils.sendHttpRequest(url, "GET", token, null);
            String responseBody = response.body();

            HttpUtils.checkIfResponseWasUnauthorized(response);

            UsersResponse usersResponse = HttpUtils.parseJsonResponse(responseBody, UsersResponse.class);
            return usersResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to fetch user details by user ID from the backend API.
     *
     * @param userId - Integer value representing the user ID.
     * @return User object.
     */
    public User getUserById(Integer userId) {
        try {
            String url = "http://localhost:8080/users/" + userId;
            String token = JwtTokenService.getInstance().getJwtToken();

            HttpResponse<String> response = HttpUtils.sendHttpRequest(url, "GET", token, null);
            String responseBody = response.body();
            HttpUtils.checkIfResponseWasUnauthorized(response);

            UserResponse userResponse = HttpUtils.parseJsonResponse(responseBody, UserResponse.class);
            return userResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to update user details by user ID using the backend API.
     *
     * @param userId         - Integer value representing the user ID.
     * @param updateUserDto - UpdateUserDto object containing the updated user details.
     * @return boolean value indicating the success of the operation.
     */
    public boolean updateUser(Integer userId, UpdateUserDto updateUserDto) {
        try {
            String url = "http://localhost:8080/users/" + userId;
            String token = JwtTokenService.getInstance().getJwtToken();

            Gson gson = new Gson();
            String requestBody = gson.toJson(updateUserDto);

            HttpResponse<String> response = HttpUtils.sendHttpRequest(url, "PUT", token, requestBody);

            HttpUtils.checkIfResponseWasUnauthorized(response);

            int statusCode = response.statusCode();

            return response.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method to delete a user by user ID using the backend API.
     *
     * @param userId - Integer value representing the user ID.
     * @return boolean value indicating the success of the operation.
     */
    public static boolean deleteUser(Integer userId) {
        try {
            String url = "http://localhost:8080/users/" + userId;
            String token = JwtTokenService.getInstance().getJwtToken();

            HttpResponse<String> response = HttpUtils.sendHttpRequest(url, "DELETE", token, null);
            HttpUtils.checkIfResponseWasUnauthorized(response);
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

