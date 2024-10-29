package com.food.backend.controller;

import com.food.backend.dto.NotificationRequestDto;
import com.food.backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Endpoints for sending notifications to a topic")
@Validated
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(
            summary = "Sends Notification to all clients within a topic",
            description = "Every client subscribed to the topic will receive the notification"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden, only managers can send notifications"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/send/to/topic/{topic}")
    @PreAuthorize("hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    public String sendNotification(
            @PathVariable @NotNull String topic,
            @RequestBody @Valid NotificationRequestDto notificationRequestDto
    ) {
        return notificationService.sendNotificationToTopic(
                topic,
                notificationRequestDto.getTitle(),
                notificationRequestDto.getBody(),
                notificationRequestDto.getImage()
        );
    }
}