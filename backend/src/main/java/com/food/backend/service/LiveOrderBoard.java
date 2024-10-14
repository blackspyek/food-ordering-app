package com.food.backend.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.SortedSet;
import java.util.TreeSet;

@Getter
@Service
public class LiveOrderBoard {
    private final SortedSet<String> liveOrderBoardCodes;
    private static Integer LAST_NUMBER = -1;

    public LiveOrderBoard() {
        liveOrderBoardCodes = new TreeSet<>();
    }

    public String generateOrderBoardCode() {
        if (++LAST_NUMBER == 100)  LAST_NUMBER = 0;
        String orderCode = LAST_NUMBER.toString();
        addNewOrderCode(orderCode);
        return orderCode;
    }
    private void addNewOrderCode(String orderCode) {
        liveOrderBoardCodes.add(orderCode);
    }
    public void removeOrderCode(String orderCode) {
        liveOrderBoardCodes.remove(orderCode);
    }
}
