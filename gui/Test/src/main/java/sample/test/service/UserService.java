package sample.test.service;

import com.google.gson.Gson;
import sample.test.controllers.EmployeeViewController;
import sample.test.dto.UpdateUserDto;
import sample.test.model.User;
import sample.test.utils.HttpUtils;

import java.net.http.HttpResponse;
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

    public List<User> getUsers() {
        try {
            String url = "http://localhost:8080/users/";
            String token = JwtTokenService.getInstance().getJwtToken();

            HttpResponse<String> response = HttpUtils.sendHttpRequest(url, "GET", token, null);
            String responseBody = response.body();
            List<User> users = parseJsonResponse(responseBody);
            return users;
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
            User user = parseSingleUserResponse(responseBody);
            return user;
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

            int statusCode = response.statusCode();

            return response.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(Integer userId) {
        try {
            String url = "http://localhost:8080/users/" + userId;
            String token = JwtTokenService.getInstance().getJwtToken();

            HttpResponse<String> response = HttpUtils.sendHttpRequest(url, "DELETE", token, null);
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<User> parseJsonResponse(String jsonResponse) {
        Gson gson = new Gson();
        UserResponse userResponse = gson.fromJson(jsonResponse, UserResponse.class);
        return userResponse.getData();
    }

    private static class UserResponse {
        private List<User> data;

        public List<User> getData() {
            return data;
        }

        public void setData(List<User> data) {
            this.data = data;
        }
    }

    private User parseSingleUserResponse(String jsonResponse) {
        Gson gson = new Gson();
        UserResponseForSingleUser response = gson.fromJson(jsonResponse, UserResponseForSingleUser.class);
        return response.getData();
    }

    private static class UserResponseForSingleUser {
        private User data;

        public User getData() {
            return data;
        }

        public void setData(User data) {
            this.data = data;
        }
    }
}

