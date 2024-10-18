package com.food.backend.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Getter
@Service
public class LiveOrderBoard {
    private final SortedSet<String> liveOrderBoardCodes;
    private final Set<String> liveOrderBoardReadyCodes;
    private static Integer LAST_NUMBER = -1;

    public LiveOrderBoard() {
        liveOrderBoardCodes = new TreeSet<>();
        liveOrderBoardReadyCodes = new HashSet<>();
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
    public void moveOrderCodeToReady(String orderCode) {
        liveOrderBoardCodes.remove(orderCode);
        liveOrderBoardReadyCodes.add(orderCode);
    }
    public void removeOrderCode(String orderCode) {
        liveOrderBoardCodes.remove(orderCode);
        liveOrderBoardReadyCodes.remove(orderCode);
    }
}
