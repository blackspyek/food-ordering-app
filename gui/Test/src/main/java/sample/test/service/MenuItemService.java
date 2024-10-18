package sample.test.service;

import com.google.gson.Gson;
import sample.test.model.MenuItem;
import sample.test.utils.HttpUtils;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class MenuItemService {

    public static List<MenuItem> getMenuItems() {
        try {
            String url = "http://localhost:8080/api/menu/";
            String token = JwtTokenService.getInstance().getJwtToken();
            HttpResponse<String> response = HttpUtils.sendHttpRequest(url, "GET", token, null);
            String responseBody = response.body();
            MenuItem[] menuItemsArray = new Gson().fromJson(responseBody, MenuItem[].class);
            return Arrays.asList(menuItemsArray);
        } catch (Exception e) {
            return null;
        }
    }

    public class MenuItemsResponse {
        private List<MenuItem> data;

        public List<MenuItem> getData() {
            return data;
        }

        public void setData(List<MenuItem> data) {
            this.data = data;
        }
    }
}
