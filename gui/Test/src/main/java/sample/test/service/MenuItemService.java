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

/**
 * Service class responsible for managing menu items in the application.
 * <p>
 * This class interacts with the backend API to perform CRUD operations on menu items. It provides methods to
 * retrieve, add, update, delete, and set the availability of menu items. The service utilizes Gson for JSON
 * serialization and deserialization, and handles HTTP requests through utility methods in the HttpUtils class.
 * <p>
 * Key responsibilities include:
 * - Sending HTTP requests to the backend API for menu item operations.
 * - Handling API responses, including checking for authorization and converting response bodies to Java objects.
 * - Providing methods to convert category strings into enum values.
 * <p>
 * This class employs a singleton pattern for the JwtTokenService to ensure that the JWT token is sent with
 * each request for authentication.
 */

public class MenuItemService {

    private static final String BASE_URL = "http://localhost:8080/api/menu/";
    private static final Gson gson = new Gson();

    /**
     * Sends an HTTP request to the specified URL with the given method and request body.
     * <p>
     * This method uses the JwtTokenService to retrieve the JWT token for authentication. If the token is
     * successfully retrieved, the request is sent using the HttpUtils class. If an exception occurs, an error
     * message is printed and null is returned.
     *
     * @param url         the URL to send the request to
     * @param method      the HTTP method to use (GET, POST, PUT, DELETE, PATCH)
     * @param requestBody the request body to send with the request
     * @return the HTTP response received from the server, or null if an exception occurs
     */
    private static HttpResponse<String> sendHttpRequest(String url, String method, String requestBody) {
        try {
            String token = JwtTokenService.getInstance().getJwtToken();
            return HttpUtils.sendHttpRequest(url, method, token, requestBody);
        } catch (Exception e) {
            System.out.println("Failed to send " + method + " request to " + url);
            return null;
        }
    }

    /**
     * Handles the response received from an HTTP request, checking for authorization and a successful status code.
     * <p>
     * If the response is unauthorized, the method throws an IOException. If the response status code matches the
     * success code, the method returns true. Otherwise, an error message is printed and false is returned.
     *
     * @param response    the HTTP response received from the server
     * @param successCode the expected success status code
     * @return true if the response is successful, false otherwise
     * @throws IOException if the response is unauthorized
     */
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

    /**
     * Handles the response received from a GET request, checking for authorization and a successful status code.
     * <p>
     * If the response is unauthorized, the method throws an IOException. If the response status code matches the
     * success code, the response body is converted to a Java object using the provided converter function. Otherwise,
     * an error message is printed and null is returned.
     *
     * @param response  the HTTP response received from the server
     * @param converter the function to convert the response body to a Java object
     * @param <T>       the type of object to convert the response body to
     * @return the Java object converted from the response body, or null if the response is unsuccessful
     * @throws IOException if the response is unauthorized
     */
    private static <T> T handleGetResponse(HttpResponse<String> response, Function<String, T> converter) throws IOException {
        HttpUtils.checkIfResponseWasUnauthorized(response);
        if (response.statusCode() == 200) {
            return converter.apply(response.body());
        } else {
            System.out.println("Error: " + response.statusCode() + " for GET request.");
            return null;
        }
    }

    /**
     * Retrieves all menu items from the backend API.
     * <p>
     * This method sends a GET request to the API to retrieve all menu items. If the request is successful, the
     * response body is converted to a list of MenuItem objects and returned. If an exception occurs, an error message
     * is printed and null is returned.
     *
     * @return a list of all menu items retrieved from the API, or null if an exception occurs
     */
    public static List<MenuItem> getMenuItems() throws IOException {
        HttpResponse<String> response = sendHttpRequest(BASE_URL, "GET", null);
        return handleGetResponse(response, body -> {
            MenuItem[] menuItemsArray = gson.fromJson(body, MenuItem[].class);
            return Arrays.asList(menuItemsArray);
        });
    }

    /**
     * Retrieves a menu item by its ID from the backend API.
     * <p>
     * This method sends a GET request to the API to retrieve a menu item by its ID. If the request is successful, the
     * response body is converted to a MenuItem object and returned. If an exception occurs, an error message is printed
     * and null is returned.
     *
     * @param id the ID of the menu item to retrieve
     * @return the menu item retrieved from the API, or null if an exception occurs
     */
    public static MenuItem getMenuItemById(Long id) throws IOException {
        HttpResponse<String> response = sendHttpRequest(BASE_URL + id, "GET", null);
        return handleGetResponse(response, body -> {
            MenuItemResponse menuItemResponse = gson.fromJson(body, MenuItemResponse.class);
            return menuItemResponse.getData();
        });
    }

    /**
     * Adds a new menu item to the backend API.
     * <p>
     * This method sends a POST request to the API to add a new menu item. The request body contains the details of
     * the menu item to add. If the request is successful, the method returns true. If an exception occurs, an error
     * message is printed and false is returned.
     *
     * @param menuItemDto the details of the menu item to add
     * @return true if the menu item is added successfully, false otherwise
     */
    public static boolean addMenuItem(MenuItemDto menuItemDto) throws IOException {
        String requestBody = gson.toJson(menuItemDto);
        HttpResponse<String> response = sendHttpRequest(BASE_URL, "POST", requestBody);
        return handleResponse(response, 200);
    }

    /**
     * Updates an existing menu item in the backend API.
     * <p>
     * This method sends a PUT request to the API to update an existing menu item. The request body contains the
     * details of the menu item to update. If the request is successful, the method returns true. If an exception
     * occurs, an error message is printed and false is returned.
     *
     * @param menuItemDto the details of the menu item to update
     * @param id          the ID of the menu item to update
     * @return true if the menu item is updated successfully, false otherwise
     */
    public static boolean updateMenuItem(MenuItemDto menuItemDto, Long id) throws IOException {
        String requestBody = gson.toJson(menuItemDto);
        HttpResponse<String> response = sendHttpRequest(BASE_URL + id, "PUT", requestBody);
        return handleResponse(response, 200);
    }

    /**
     * Sets the availability of a menu item in the backend API.
     * <p>
     * This method sends a PATCH request to the API to set the availability of a menu item. If the request is
     * successful, the method returns true. If an exception occurs, an error message is printed and false is returned.
     *
     * @param id the ID of the menu item to set the availability of
     * @return true if the availability is set successfully, false otherwise
     */
    public static boolean setMenuItemAvailability(Long id) throws IOException {
        HttpResponse<String> response = sendHttpRequest(BASE_URL + "availability/" + id, "PATCH", null);
        return handleResponse(response, 200);
    }

    /**
     * Deletes a menu item from the backend API.
     * <p>
     * This method sends a DELETE request to the API to delete a menu item by its ID. If the request is successful,
     * the method returns true. If an exception occurs, an error message is printed and false is returned.
     *
     * @param id the ID of the menu item to delete
     * @return true if the menu item is deleted successfully, false otherwise
     */
    public static boolean deleteMenuItem(Long id) throws IOException {
        HttpResponse<String> response = sendHttpRequest(BASE_URL + id, "DELETE", null);
        return handleResponse(response, 200);
    }

    /**
     * Converts a category string into a Category enum value.
     * <p>
     * This method attempts to convert the given category string into a Category enum value. If the conversion is
     * successful, the method returns the corresponding Category value. If the category is invalid, an error message
     * is printed and null is returned.
     *
     * @param category the category string to convert
     * @return the Category enum value corresponding to the category string, or null if the category is invalid
     */
    public static Category convertCategory(String category) {
        try {
            return Category.valueOf(category);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid category received: " + category);
            return null;
        }
    }
}
