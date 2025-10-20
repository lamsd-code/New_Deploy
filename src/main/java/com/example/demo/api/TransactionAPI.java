package com.example.demo.api;

import com.example.demo.model.request.TransactionCreateRequest;
import com.example.demo.model.response.ResponseDTO;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.security.utils.SecurityUtils;
import com.example.demo.model.dto.MyUserDetail;
import com.example.demo.service.TransactionService;
import com.example.demo.service.VnpayService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ TransactionAPI — Quản lý thêm, sửa, xóa giao dịch.
 * Đảm bảo an toàn với mọi trường hợp (chưa login, session hết hạn, v.v.)
 */
@RestController
@Transactional
@RequestMapping("/api/transaction")
public class TransactionAPI {

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private VnpayService vnpayService;


    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * 🧩 Thêm hoặc cập nhật giao dịch.
     */
    @PostMapping
    public ResponseDTO addOrUpdateTransaction(@RequestBody TransactionCreateRequest transactionCreateRequest) {
        ResponseDTO response = new ResponseDTO();

        // ✅ Lấy thông tin nhân viên hiện tại (từ SecurityContext)
        MyUserDetail principal = SecurityUtils.getPrincipal();

        // ⚠️ Nếu chưa đăng nhập hoặc session hết hạn
        if (principal == null || principal.getId() == null) {
            response.setMessage("⚠️ Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại!");
            return response;
        }

        Long staffId = principal.getId();

        try {
            // Gọi service lưu giao dịch
            ResponseDTO result = transactionService.save(transactionCreateRequest, staffId);
            response.setMessage(result.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("❌ Lưu giao dịch thất bại: " + e.getMessage());
        }

        return response;
    }

    /**
     * 📋 Lấy thông tin giao dịch cũ theo ID.
     */
    @GetMapping("/{id}")
    public ResponseDTO getOldTransaction(@PathVariable Long id) {
        ResponseDTO response = new ResponseDTO();

        try {
            response = transactionService.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("❌ Không tìm thấy giao dịch có ID = " + id);
        }

        return response;
    }

    /**
     * 🗑️ Xóa giao dịch theo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseDTO deleteTransaction(@PathVariable Long id) {
        ResponseDTO response = new ResponseDTO();

        try {
            if (!transactionRepository.existsById(id)) {
                response.setMessage("⚠️ Giao dịch không tồn tại hoặc đã bị xóa trước đó.");
                return response;
            }

            transactionRepository.deleteById(id);
            response.setMessage("🗑️ Xóa giao dịch thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("❌ Xóa giao dịch thất bại: " + e.getMessage());
        }

        return response;
    }
    @PostMapping("/vnpay")
    public ResponseDTO createVnpayTransaction(@RequestBody TransactionCreateRequest request) {
        return vnpayService.createPayment(request);
    }

}
