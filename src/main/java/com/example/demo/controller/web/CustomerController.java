package com.example.demo.controller.web;

import com.example.demo.model.dto.CustomerDTO;
import com.example.demo.model.request.CustomerCreateRequest;
import com.example.demo.model.response.ResponseDTO;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ✅ Controller cho khách hàng (website public)
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/web")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // 📨 Khách hàng gửi form liên hệ từ trang web
    @PostMapping("/contact")
    public ResponseEntity<?> createCustomerFromWeb(@RequestBody CustomerCreateRequest request) {
        try {
        	ResponseDTO response = customerService.save(request);
        	return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể lưu thông tin khách hàng: " + e.getMessage());
        }
    }
}
