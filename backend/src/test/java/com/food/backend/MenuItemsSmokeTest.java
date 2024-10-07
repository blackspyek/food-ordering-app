package com.food.backend;

import com.food.backend.controller.MenuItemController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MenuItemsSmokeTest {

    @Autowired
    private MenuItemController menuItemController;

    @Test
    void contextLoads() throws Exception {
        assertThat(menuItemController).isNotNull();
    }
}
