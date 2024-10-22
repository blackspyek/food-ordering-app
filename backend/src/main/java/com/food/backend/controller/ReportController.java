package com.food.backend.controller;

import com.food.backend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/reports")
@RestController
@PreAuthorize("hasRole('MANAGER')")
@Tag(name = "Reports", description = "Endpoints for generating sales reports")
public class ReportController {

    private final ReportService reportService;


    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily/json")
    @Operation(
            summary = "Generate daily report",
            description = "Generates a report for the current day's sales. Requires manager role",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report generated successfully",
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"totalOrders\": 5, \"totalAmount\": 100.0, \"averageAmount\": 20.0, \"salesByCategory\": {\"PIZZA\": 3, \"BURGER\": 2}}"))
            ),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Error generating report")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<byte[]> generateDailyReport() {
        return reportService.generateDailyReport();
    }

    @GetMapping("/weekly/json")
    @Operation(
            summary = "Generate weekly report",
            description = "Generates a report for the current week's sales. Requires manager role",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report generated successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"totalOrders\": 5, \"totalAmount\": 100.0, \"averageAmount\": 20.0, \"salesByCategory\": {\"PIZZA\": 3, \"BURGER\": 2}}"))
            ),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "400", description = "Error generating report")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<byte[]> generateWeeklyReport() {
        return reportService.generateWeeklyReport();
    }
}
