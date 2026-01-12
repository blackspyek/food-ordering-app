package com.food.backend.controller;

import com.food.backend.service.interfaces.ILiveOrderBoard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
@Tag(name = "Live Order Board Events", description = "WebSocket endpoints for real-time order board updates")
@RequiredArgsConstructor
public class EventController {
    private final ILiveOrderBoard liveOrderBoard;


    @Operation(
            summary = "Subscribe to order board updates",
            description = "WebSocket endpoint that broadcasts the current state of the order board to all subscribers"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully subscribed to order board updates",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(
                                    type = "string",
                                    example = "\"liveOrderBoardCodes\": [4,5,6], \"readyOrderBoardCodes\": [1,2,3]"
                            )
                    )
            )
    })
    @MessageMapping("/sendMessage")
    @SendTo("/topic/orderBoard")
    public String handleMessage() {
        return liveOrderBoard.getOrderBoardState().toString();
    }
}
