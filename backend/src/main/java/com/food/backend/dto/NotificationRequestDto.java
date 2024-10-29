package com.food.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDto {

    @NotEmpty(message = "Title is required")
    private String title;

    @NotEmpty(message = "Body is required")
    private String body;

    @Pattern(regexp = "^(https?)://[^\\s/$.?#].[^\\s]*$",
            message = "Image must be a valid URL starting with http or https")
    private String image;
}
