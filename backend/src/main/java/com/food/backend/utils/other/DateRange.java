package com.food.backend.utils.other;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DateRange(LocalDateTime start, LocalDateTime end) {
    /**
     * Creates a DateRange for the current day (00:00:00 to 23:59:59)
     */
    public static DateRange forDaily() {
        LocalDate today = LocalDate.now();
        return new DateRange(
                today.atStartOfDay(),
                today.atTime(23, 59, 59)
        );
    }

    /**
     * Creates a DateRange for the current week (Monday 00:00:00 to Sunday 23:59:59)
     */
    public static DateRange forWeekly() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(7);
        return new DateRange(
                startOfWeek.atStartOfDay(),
                today.atTime(23, 59, 59)
        );
    }

    /**
     * Validates that the start date is before or equal to the end date
     */
    public DateRange {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
    }
}