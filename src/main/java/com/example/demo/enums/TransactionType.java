package com.example.demo.enums;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public enum TransactionType {
	// 🔹 Dành cho nhân viên nội bộ
    CSKH("Chăm sóc khách hàng"),
    DDX("Dẫn đi xem"),
	
	// 🔹 Dành cho khách hàng (frontend)
    DEMO_VISIT("Đặt lịch xem tòa nhà"),
    RENT_PAYMENT("Thanh toán tiền thuê / đặt cọc");

    private final String name;
    TransactionType(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public static Map<String, String> transactionType() {
        Map<String, String> transactionList = new LinkedHashMap<>();
        for (TransactionType i : TransactionType.values()) {
            transactionList.put(i.name(), i.getName());
        }
        return transactionList;
    }
}
