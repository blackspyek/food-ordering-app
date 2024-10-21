package com.food.backend.controllers;

import com.food.backend.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    private byte[] sampleReportData;
    private static final String DAILY_REPORT_ENDPOINT = "/api/reports/daily/json";
    private static final String WEEKLY_REPORT_ENDPOINT = "/api/reports/weekly/json";

    @BeforeEach
    void setUp() {
        // Sample JSON report data
        String jsonReport = "{\"totalOrders\": 5, \"totalAmount\": 100.0, \"averageAmount\": 20.0, " +
                "\"salesByCategory\": {\"PIZZA\": 3, \"BURGER\": 2}}";
        sampleReportData = jsonReport.getBytes();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void generateDailyReport_WithManagerRole_ShouldSucceed() throws Exception {
        when(reportService.generateDailyReport())
                .thenReturn(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(sampleReportData));

        mockMvc.perform(get(DAILY_REPORT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().bytes(sampleReportData));

        verify(reportService, times(1)).generateDailyReport();
    }

    @Test
    @WithMockUser(roles = "USER")
    void generateDailyReport_WithUserRole_ShouldFail() throws Exception {
        mockMvc.perform(get(DAILY_REPORT_ENDPOINT))
                .andExpect(status().isForbidden());

        verify(reportService, never()).generateDailyReport();
    }

    @Test
    void generateDailyReport_WithoutAuthentication_ShouldFail() throws Exception {
        mockMvc.perform(get(DAILY_REPORT_ENDPOINT))
                .andExpect(status().isUnauthorized());

        verify(reportService, never()).generateDailyReport();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void generateDailyReport_WhenServiceFails_ShouldReturn500() throws Exception {
        when(reportService.generateDailyReport())
                .thenReturn(ResponseEntity.internalServerError().build());

        mockMvc.perform(get(DAILY_REPORT_ENDPOINT))
                .andExpect(status().isInternalServerError());

        verify(reportService, times(1)).generateDailyReport();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void generateWeeklyReport_WithManagerRole_ShouldSucceed() throws Exception {
        when(reportService.generateWeeklyReport())
                .thenReturn(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(sampleReportData));

        mockMvc.perform(get(WEEKLY_REPORT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().bytes(sampleReportData));

        verify(reportService, times(1)).generateWeeklyReport();
    }

    @Test
    @WithMockUser(roles = "USER")
    void generateWeeklyReport_WithUserRole_ShouldFail() throws Exception {
        mockMvc.perform(get(WEEKLY_REPORT_ENDPOINT))
                .andExpect(status().isForbidden());

        verify(reportService, never()).generateWeeklyReport();
    }

    @Test
    void generateWeeklyReport_WithoutAuthentication_ShouldFail() throws Exception {
        mockMvc.perform(get(WEEKLY_REPORT_ENDPOINT))
                .andExpect(status().isUnauthorized());

        verify(reportService, never()).generateWeeklyReport();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void generateWeeklyReport_WhenServiceFails_ShouldReturn500() throws Exception {
        when(reportService.generateWeeklyReport())
                .thenReturn(ResponseEntity.internalServerError().build());

        mockMvc.perform(get(WEEKLY_REPORT_ENDPOINT))
                .andExpect(status().isInternalServerError());

        verify(reportService, times(1)).generateWeeklyReport();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void generateDailyReport_ShouldIncludeCorrectContentType() throws Exception {
        when(reportService.generateDailyReport())
                .thenReturn(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(sampleReportData));

        mockMvc.perform(get(DAILY_REPORT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        verify(reportService, times(1)).generateDailyReport();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void generateWeeklyReport_ShouldIncludeCorrectContentType() throws Exception {
        when(reportService.generateWeeklyReport())
                .thenReturn(ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(sampleReportData));

        mockMvc.perform(get(WEEKLY_REPORT_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        verify(reportService, times(1)).generateWeeklyReport();
    }
}
