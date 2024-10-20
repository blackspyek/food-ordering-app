package com.example.android.api;
import com.example.android.dto.CreateOrderDto;
import com.example.android.dto.CreateOrderResponse;
import com.example.android.models.MenuItem;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ApiService {
    @GET("api/menu/")
    Call<List<MenuItem>> getMenuItems();

    @GET("api/menu/available")
    Call<List<MenuItem>> getAvailableMenuItems();

    @GET("api/menu/category/{category}")
    Call<List<MenuItem>> getMenuItemsByCategory(@Path("category") String category);

    @GET("api/menu/name/{name}")
    Call<MenuItem> getMenuItemByName(@Path("name") String name);

    @POST("api/orders")
    Call<CreateOrderResponse> createOrder(@Body CreateOrderDto createOrderDto);

}
