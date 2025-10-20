package com.example.demo.service;

import com.example.demo.model.request.TransactionCreateRequest;
import com.example.demo.model.response.ResponseDTO;

import java.util.Map;

public interface VnpayService {

    /**
     * 🔹 Tạo URL thanh toán VNPAY (sandbox hoặc production)
     *  - Lưu transaction (PENDING)
     *  - Sinh URL có chữ ký HMAC SHA512
     *  - Trả về link redirect để frontend chuyển người dùng đến VNPAY
     */
    ResponseDTO createPayment(TransactionCreateRequest req);

    /**
     * 🔹 Xử lý callback khi VNPAY redirect về hệ thống
     *  - Kiểm tra hash hợp lệ
     *  - Cập nhật trạng thái Transaction (SUCCESS / FAILED)
     *  - Trả về map chứa thông tin giao dịch để hiển thị ra view
     */
    Map<String, Object> handleReturn(Map<String, String> params);
}
