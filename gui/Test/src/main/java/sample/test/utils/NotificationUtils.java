package sample.test.utils;

import com.google.gson.Gson;
import sample.test.dto.NotificationRequestDto;
import sample.test.service.JwtTokenService;

import java.io.IOException;
import java.net.http.HttpResponse;

public class NotificationUtils {
    private final static String METHOD = "POST";
    private final static String FCM_URL = "http://localhost:8080/api/notifications/send/to/topic/";

    public static String sendNotification(
            String topic,
            NotificationRequestDto notificationRequestDto
    ) throws Exception {

            getRequirements result = getGetRequirements(topic, notificationRequestDto);
            HttpResponse<String> response = HttpUtils.sendHttpRequest(result.url(), METHOD, result.token(), result.jsonInputString());
            return checkResponse(response);


    }

    private static getRequirements getGetRequirements(String topic, NotificationRequestDto notificationRequestDto) {
        String url = FCM_URL + topic;
        String token = JwtTokenService.getInstance().getJwtToken();
        String jsonInputString = new Gson().toJson(notificationRequestDto);
        getRequirements result = new getRequirements(url, token, jsonInputString);
        return result;
    }

    private record getRequirements(String url, String token, String jsonInputString) {
    }

    private static String checkResponse(HttpResponse<String> response) throws IOException {
        if (HttpUtils.checkIfResponseWasGood(response)) {
            return response.body();
        }
        else if (HttpUtils.checkIfResponseWasUnauthorized(response)) {
            throw new IOException("Unauthorized");
        }
        else {
            throw new IOException("Failed to send notification");
        }
    }
}
