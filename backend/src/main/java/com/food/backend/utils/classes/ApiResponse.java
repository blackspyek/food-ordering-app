package com.food.backend.utils.classes;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final T data;
    private final String message;
    private final int status;

    public ApiResponse(T data, String message, int status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

}
