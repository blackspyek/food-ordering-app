package sample.test.utils;

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
        if ("POST".equalsIgnoreCase(method) && jsonInputString != null) {
            requestBuilder.header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInputString));
        } else if ("GET".equalsIgnoreCase(method)) {
            requestBuilder.GET();
        } else if ("PUT".equalsIgnoreCase(method) && jsonInputString != null) {
            requestBuilder.header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonInputString));
        } else if ("DELETE".equalsIgnoreCase(method)) {
            requestBuilder.DELETE();
        } else {
            throw new UnsupportedOperationException("HTTP method not supported: " + method);
        }

        HttpRequest request = requestBuilder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
