package sample.test.service;

import com.google.gson.Gson;
import sample.test.dto.MenuItemDto;
import sample.test.helpers.MenuItemResponse;
import sample.test.model.Category;
import sample.test.model.MenuItem;
import sample.test.utils.HttpUtils;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class MenuItemService {

    private static final String BASE_URL = "http://localhost:8080/api/menu/";
    private static final Gson gson = new Gson();

    private static HttpResponse<String> sendHttpRequest(String url, String method, String requestBody) {
        try {
            String token = JwtTokenService.getInstance().getJwtToken();
            return HttpUtils.sendHttpRequest(url, method, token, requestBody);
        } catch (Exception e) {
            System.out.println("Failed to send " + method + " request to " + url);
            return null;
        }
    }

    private static boolean handleResponse(HttpResponse<String> response, int successCode) throws IOException {
        HttpUtils.checkIfResponseWasUnauthorized(response);
        if (response.statusCode() == successCode) {
            return true;
        } else {
            System.out.println("Error: " + response.statusCode() +
                    " for request.");
            return false;
        }
    }

    private static <T> T handleGetResponse(HttpResponse<String> response, Function<String, T> converter) throws IOException {
        HttpUtils.checkIfResponseWasUnauthorized(response);
        if (response.statusCode() == 200) {
            return converter.apply(response.body());
        } else {
            System.out.println("Error: " + response.statusCode() + " for GET request.");
            return null;
        }
    }

    public static List<MenuItem> getMenuItems() throws IOException {
        HttpResponse<String> response = sendHttpRequest(BASE_URL, "GET", null);
        return handleGetResponse(response, body -> {
            MenuItem[] menuItemsArray = gson.fromJson(body, MenuItem[].class);
            return Arrays.asList(menuItemsArray);
        });
    }

    public static MenuItem getMenuItemById(Long id) throws IOException {
        HttpResponse<String> response = sendHttpRequest(BASE_URL + id, "GET", null);
        return handleGetResponse(response, body -> {
            MenuItemResponse menuItemResponse = gson.fromJson(body, MenuItemResponse.class);
            return menuItemResponse.getData();
        });
    }

    public static boolean addMenuItem(MenuItemDto menuItemDto) throws IOException {
        String requestBody = gson.toJson(menuItemDto);
        HttpResponse<String> response = sendHttpRequest(BASE_URL, "POST", requestBody);
        return handleResponse(response, 200);
    }

    public static boolean updateMenuItem(MenuItemDto menuItemDto, Long id) throws IOException {
        String requestBody = gson.toJson(menuItemDto);
        HttpResponse<String> response = sendHttpRequest(BASE_URL + id, "PUT", requestBody);
        return handleResponse(response, 200);
    }

    public static boolean setMenuItemAvailability(Long id) throws IOException {
        HttpResponse<String> response = sendHttpRequest(BASE_URL + "availability/" + id, "PATCH", null);
        return handleResponse(response, 200);
    }

    public static boolean deleteMenuItem(Long id) throws IOException {
        HttpResponse<String> response = sendHttpRequest(BASE_URL + id, "DELETE", null);
        return handleResponse(response, 200);
    }

    public static Category convertCategory(String category) {
        try {
            return Category.valueOf(category);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid category received: " + category);
            return null;
        }
    }
}
