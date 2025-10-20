package com.example.demo.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionCreateRequest {
    private Long id;
    private Long customerId;
    private String note;
    private String code;
    private String type;      // DEMO_VISIT / RENT_PAYMENT / CSKH / DDX
    private String status;    // BOOKED / PENDING / SUCCESS / FAILED
    private Long buildingId;  // tòa nhà khách chọn
    private Long amount; 
}
