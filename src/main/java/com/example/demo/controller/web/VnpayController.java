package com.example.demo.controller.web;

import com.example.demo.service.VnpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * ✅ Controller nhận callback từ VNPAY (sau khi thanh toán)
 *  - Không yêu cầu xác thực (public endpoint)
 */
@Controller
public class VnpayController {

    @Autowired
    private VnpayService vnpayService;

    /**
     * 🔹 Endpoint callback từ VNPAY sau khi khách hàng thanh toán xong
     *  → Ví dụ: http://localhost:8080/vnpay_return?vnp_ResponseCode=00&vnp_TxnRef=5&vnp_Amount=1000000
     */
    @GetMapping("/vnpay_return")
    public ModelAndView vnpayReturn(@RequestParam Map<String, String> params) {
        Map<String, Object> result = vnpayService.handleReturn(params);

        ModelAndView mav = new ModelAndView();
        mav.addObject("transactionInfo", result);

        if ((boolean) result.getOrDefault("success", false)) {
            mav.setViewName("web/payment-success");  // ✅ templates/web/payment-success.html
        } else {
            mav.setViewName("web/payment-fail");
        }

        return mav;
    }

    /**
     * ✅ Test thủ công nếu chưa tích hợp VNPAY Sandbox (tuỳ chọn)
     */
    @GetMapping("/payment-success")
    public String paymentSuccess() {
        return "web/payment-success";
    }

    @GetMapping("/payment-fail")
    public String paymentFail() {
        return "web/payment-fail";
    }
}
