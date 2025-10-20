package com.example.demo.service.impl;

import com.example.demo.converter.TransactionConverter;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Transaction;
import com.example.demo.model.dto.TransactionDTO;
import com.example.demo.model.request.TransactionCreateRequest;
import com.example.demo.model.response.ResponseDTO;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ✅ TransactionServiceImpl
 * Quản lý nghiệp vụ giao dịch: CSKH, DDX, RENT_PAYMENT, DEMO_VISIT...
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionConverter transactionConverter;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * 🔹 Lưu hoặc cập nhật giao dịch
     */
    @Override
    public ResponseDTO save(TransactionCreateRequest transactionCreateRequest, Long staffId) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Transaction transaction = transactionConverter.toTransactionEntity(transactionCreateRequest, staffId);

            // ⚙️ Mặc định trạng thái nếu chưa có
            if (transaction.getStatus() == null || transaction.getStatus().isEmpty()) {
                transaction.setStatus("PENDING");
            }

            transactionRepository.save(transaction);

            responseDTO.setMessage(transactionCreateRequest.getId() == null ?
                    "✅ Thêm giao dịch thành công" : "✅ Cập nhật giao dịch thành công");
            responseDTO.setData(transaction.getId());

        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setMessage("❌ Lưu giao dịch thất bại: " + e.getMessage());
        }
        return responseDTO;
    }

    /**
     * 🔹 Lấy tất cả giao dịch theo loại & khách hàng
     */
    @Override
    public List<TransactionDTO> findAllByCodeAndCustomer(String code, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng id = " + customerId));

        List<Transaction> transactionEntities = transactionRepository.findAllByCodeAndCustomer(code, customer);
        List<TransactionDTO> transactionDTOS = new ArrayList<>();

        for (Transaction t : transactionEntities) {
            transactionDTOS.add(transactionConverter.toTransactionDTO(t));
        }
        return transactionDTOS;
    }

    /**
     * 🔹 Lấy ghi chú cũ (phục vụ khi sửa)
     */
    @Override
    public ResponseDTO findById(Long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        Transaction transactionEntity = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch id = " + id));

        responseDTO.setMessage("success");
        responseDTO.setData(transactionEntity.getNote());
        return responseDTO;
    }

    /**
     * 🔹 (Tùy chọn) Tạo mới giao dịch thanh toán VNPAY
     */
    public Transaction createPaymentTransaction(Long customerId, Long buildingId, Long amount, String type) {
        Transaction transaction = new Transaction();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng id = " + customerId));

        transaction.setCustomer(customer);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus("PENDING");
        transactionRepository.save(transaction);
        return transaction;
    }
}
