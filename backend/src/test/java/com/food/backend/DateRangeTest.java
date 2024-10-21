package com.food.backend;

import com.food.backend.utils.other.DateRange;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeTest {

    @Test
    void testForDaily() {
        DateRange dailyRange = DateRange.forDaily();

        LocalDateTime expectedStart = LocalDate.now().atStartOfDay();
        LocalDateTime expectedEnd = LocalDate.now().atTime(23, 59, 59);

        assertEquals(expectedStart, dailyRange.start());
        assertEquals(expectedEnd, dailyRange.end());
    }

    @Test
    void testForWeekly() {
        DateRange weeklyRange = DateRange.forWeekly();

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        LocalDateTime expectedStart = startOfWeek.atStartOfDay();
        LocalDateTime expectedEnd = endOfWeek.atTime(23, 59, 59);

        assertEquals(expectedStart, weeklyRange.start());
        assertEquals(expectedEnd, weeklyRange.end());
    }

    @Test
    void testInvalidDateRangeThrowsException() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(1); // End date is before start date

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DateRange(start, end)
        );

        assertEquals("Start date must be before or equal to end date", exception.getMessage());
    }

    @Test
    void testValidDateRange() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1); // End date is after start date

        DateRange validRange = new DateRange(start, end);

        assertEquals(start, validRange.start());
        assertEquals(end, validRange.end());
    }
}
