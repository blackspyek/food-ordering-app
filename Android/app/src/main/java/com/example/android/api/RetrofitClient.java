package com.example.android.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    /**
     * A singleton class field for managing Retrofit instance.
     * This instance is lazy-initialized when first requested.
     */
    private static Retrofit retrofit;
    /**
     * The base URL for all API endpoints.
     * Currently set to the Android emulator's localhost address.
     */
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    /**
     * Returns a singleton instance of Retrofit configured with custom timeouts and JSON conversion.
     *
     * This method implements the singleton pattern to ensure only one Retrofit instance
     * is created throughout the application's lifecycle. The instance is created with:
     * <ul>
     *   <li>30-second connection timeout</li>
     *   <li>30-second read timeout</li>
     *   <li>30-second write timeout</li>
     *   <li>GSON converter factory for JSON serialization/deserialization</li>
     * </ul>
     *
     * @return A configured Retrofit instance that can be used for making API calls
     *
     */

    public static Retrofit getInstance() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
