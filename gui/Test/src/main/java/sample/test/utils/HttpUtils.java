package sample.test.utils;

import com.google.gson.Gson;
import javafx.stage.Stage;
import javafx.stage.Window;
import sample.test.service.UserService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class HttpUtils {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static HttpResponse<String> sendHttpRequest(String url, String method, String token, String jsonInputString) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(new URI(url));

        if (token != null && !token.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        switch (method.toUpperCase()) {
            case "POST":
                addJsonBody(requestBuilder, jsonInputString, "POST");
                break;
            case "GET":
                requestBuilder.GET();
                break;
            case "PUT":
                addJsonBody(requestBuilder, jsonInputString, "PUT");
                break;
            case "DELETE":
                requestBuilder.DELETE();
                break;
            case "PATCH":
                addPatchBody(requestBuilder, jsonInputString);
                break;
            default:
                throw new UnsupportedOperationException("HTTP method not supported: " + method);
        }

        HttpRequest request = requestBuilder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void addJsonBody(HttpRequest.Builder requestBuilder, String jsonInputString, String method) {
        if (jsonInputString != null) {
            requestBuilder.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(jsonInputString));
        }
    }

    private static void addPatchBody(HttpRequest.Builder requestBuilder, String jsonInputString) {
        if (jsonInputString != null) {
            requestBuilder.header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonInputString));
        } else {
            requestBuilder.method("PATCH", HttpRequest.BodyPublishers.noBody());
        }
    }

    public static <T> T parseJsonResponse(String jsonResponse, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, clazz);
    }

    public static boolean checkIfResponseWasGood(HttpResponse<String> response) throws IOException {
        int responseCode = response.statusCode();
        return responseCode == HttpURLConnection.HTTP_OK;
    }

    public static boolean checkIfResponseWasUnauthorized(HttpResponse<String> response) throws IOException {
        String responseBody = response.body();
        if (responseBody.contains("\"message\": \"Token has expired\"")) {
            UserService.resetInstance();
            reloadApp();
            return true;
        }
        return false;
    }

    public static void reloadApp() throws IOException {
        Stage currentStage = (Stage) Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
        if (currentStage != null) {
            currentStage.close();
        }
        WindowUtils.loadView("login-view.fxml", "Login", false, null, false);
    }


}
