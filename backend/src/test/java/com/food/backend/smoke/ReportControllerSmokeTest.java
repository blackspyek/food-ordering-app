package com.food.backend.smoke;

import com.food.backend.controller.OrderController;
import com.food.backend.controller.ReportController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
public class ReportControllerSmokeTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThat(applicationContext.getBean(ReportController.class)).isNotNull();
    }
}
