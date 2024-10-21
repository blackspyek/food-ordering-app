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

@Setter
@Getter
@NoArgsConstructor
public class UserService {
    private static UserService instance;
    private String username;
    private List<String> userRoles;

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

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

