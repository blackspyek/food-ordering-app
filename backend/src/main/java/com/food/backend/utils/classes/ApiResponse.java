package com.food.backend.utils.classes;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "API Response wrapper")
@Getter
public class ApiResponse<T> {
    @Schema(description = "The payload of the response")
    private final T data;
    @Schema(description = "Message describing the result", example = "Operation completed successfully")
    private final String message;
    @Schema(description = "Status of the API call", example = "success")
    private final int status;

    public ApiResponse(T data, String message, int status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

}
