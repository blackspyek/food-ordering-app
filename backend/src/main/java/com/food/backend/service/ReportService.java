package com.food.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.backend.dto.ReportDto;
import com.food.backend.exception.ReportGenerationException;
import com.food.backend.model.Enums.Category;
import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.repository.OrderRepository;
import com.food.backend.utils.other.DateRange;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    public ResponseEntity<byte[]> generateDailyReport() {
        DateRange dateRange = DateRange.forDaily();
        return generateReport(dateRange, "daily_report.json");
    }

    public ResponseEntity<byte[]> generateWeeklyReport() {
        DateRange dateRange = DateRange.forWeekly();
        return generateReport(dateRange, "weekly_report.json");
    }

    private ResponseEntity<byte[]> generateReport(DateRange dateRange, String filename) {
        ReportDto report = generateReportData(dateRange);
        byte[] reportBytes = convertToJsonBytes(report);
        HttpHeaders headers = createDownloadHeaders(filename);
        return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
    }

    private ReportDto generateReportData(DateRange dateRange) {
        return ReportDto.builder()
                .totalOrders(getCompletedOrdersCount(dateRange))
                .totalAmount(getTotalSalesAmount(dateRange))
                .averageAmount(getAverageSalesAmount(dateRange))
                .salesByCategory(getCategorySales())
                .build();
    }

    private Number getCompletedOrdersCount(DateRange dateRange) {
        return orderRepository.countOrderByOrderTimeBetweenAndStatus(
                dateRange.start(),
                dateRange.end(),
                OrderStatus.PICKED_UP
        );
    }

    private Double getTotalSalesAmount(DateRange dateRange) {
        return orderRepository.sumTotalPriceByOrderTimeBetweenAndStatus(
                dateRange.start(),
                dateRange.end(),
                OrderStatus.PICKED_UP
        );
    }

    private Double getAverageSalesAmount(DateRange dateRange) {
        return orderRepository.averageTotalPriceByOrderTimeBetweenAndStatus(
                dateRange.start(),
                dateRange.end(),
                OrderStatus.PICKED_UP
        );
    }

    private Map<String, Number> getCategorySales() {
        List<Object[]> categorySalesData = orderRepository.countItemsSoldByCategory(OrderStatus.PICKED_UP);
        Map<String, Number> categorySalesMap = new HashMap<>();

        for (Object[] result : categorySalesData) {
            Category category = (Category) result[0];
            Number count = (Number) result[1];
            categorySalesMap.put(category.name(), count);
        }

        return categorySalesMap;
    }

    private byte[] convertToJsonBytes(ReportDto report) {
        try {
            return objectMapper.writeValueAsString(report).getBytes();
        } catch (Exception e) {
            throw new ReportGenerationException("Failed to generate report JSON", e);
        }
    }

    private HttpHeaders createDownloadHeaders(String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        return headers;
    }
}


