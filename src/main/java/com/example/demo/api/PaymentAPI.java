package com.example.demo.api;

import com.example.demo.model.request.TransactionCreateRequest;
import com.example.demo.model.response.ResponseDTO;
import com.example.demo.service.VnpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentAPI {

    @Autowired
    private VnpayService vnpayService;

    @PostMapping("/create")
    public ResponseDTO createPayment(@RequestBody TransactionCreateRequest req) {
        return vnpayService.createPayment(req);
    }
}
