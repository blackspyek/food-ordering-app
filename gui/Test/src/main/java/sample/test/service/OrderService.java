package sample.test.service;

import com.google.gson.Gson;
import sample.test.dto.UpdateOrderStatusDto;
import sample.test.helpers.OrderResponse;
import sample.test.helpers.OrdersResponse;
import sample.test.model.Order;
import sample.test.model.OrderItem;
import sample.test.model.OrderStatus;
import sample.test.utils.HttpUtils;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Service class responsible for managing orders in the application.
 * <p>
 * This class interacts with the backend API to perform various operations related to orders, including
 * retrieving orders, getting order items, changing order status, and deleting orders. It handles HTTP
 * requests and responses using utility methods from the HttpUtils class, and utilizes Gson for JSON
 * serialization and deserialization.
 * <p>
 * Key responsibilities include:
 * - Sending HTTP requests to the backend API for order operations.
 * - Handling API responses, including checking for unauthorized access and converting response bodies
 *   to Java objects.
 * - Providing methods to convert order status strings into enum values.
 * <p>
 * This class employs a singleton pattern for the JwtTokenService to ensure that the JWT token is sent with
 * each request for authentication.
 */
public class OrderService {

    private static final String BASE_URL = "http://localhost:8080/api/orders";
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

    private static boolean handleResponse(HttpResponse<String> response) throws IOException {
        HttpUtils.checkIfResponseWasUnauthorized(response);
        if (response.statusCode() == 200) {
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

    public static List<Order> getOrders() throws IOException {
        HttpResponse<String> response = sendHttpRequest(BASE_URL, "GET", null);
        return handleGetResponse(response, body -> HttpUtils.parseJsonResponse(body, OrdersResponse.class).getData());
    }

    public static List<OrderItem> getOrderItems(Long orderId) {
        String url = BASE_URL + "/" + orderId;
        HttpResponse<String> response = sendHttpRequest(url, "GET", null);

        if (response != null && response.body() != null) {
            OrderResponse orderResponse = HttpUtils.parseJsonResponse(response.body(), OrderResponse.class);

            if (orderResponse != null && orderResponse.getData() != null) {
                Order order = orderResponse.getData();
                return order.getOrderItems() != null ? order.getOrderItems() : new ArrayList<>();
            }
        }

        return new ArrayList<>();
    }


    public static boolean changeOrderStatus(Long orderId, UpdateOrderStatusDto updateOrderStatusDto) throws IOException {
        String url = BASE_URL + "/" + orderId + "/status";
        String requestBody = gson.toJson(updateOrderStatusDto);
        HttpResponse<String> response = sendHttpRequest(url, "PUT", requestBody);
        return handleResponse(response);
    }

    public static boolean deleteOrder(Long orderId) throws IOException {
        String url = BASE_URL + "/" + orderId;
        HttpResponse<String> response = sendHttpRequest(url, "DELETE", null);
        return handleResponse(response);
    }

    public static OrderStatus  convertOrderStatus(String status) {
        try {
            return OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid order status received: " + status);
            return null;
        }
    }
}
