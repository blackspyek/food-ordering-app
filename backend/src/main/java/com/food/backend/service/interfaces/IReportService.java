package com.food.backend.service.interfaces;

import org.springframework.http.ResponseEntity;

public interface IReportService {
    ResponseEntity<byte[]> generateDailyReport();
    ResponseEntity<byte[]> generateWeeklyReport();
}
