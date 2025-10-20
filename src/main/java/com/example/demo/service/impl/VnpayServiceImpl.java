package com.example.demo.service.impl;

import com.example.demo.entity.Transaction;
import com.example.demo.model.request.TransactionCreateRequest;
import com.example.demo.model.response.ResponseDTO;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.VnpayService;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnpayServiceImpl implements VnpayService {

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;

    @Value("${vnpay.payUrl}")
    private String vnp_PayUrl;

    private final TransactionRepository transactionRepository;

    public VnpayServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public ResponseDTO createPayment(TransactionCreateRequest req) {
        ResponseDTO response = new ResponseDTO();

        // 🧩 Lưu transaction trước
        Transaction tx = new Transaction();
        tx.setAmount(req.getAmount());
        tx.setStatus("PENDING");
        tx.setType(req.getType());
        tx.setCode(req.getCode());
        tx.setNote(req.getNote());
        transactionRepository.save(tx);

        // 🧩 Sinh URL thanh toán
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(req.getAmount() * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(tx.getId()));
        vnp_Params.put("vnp_OrderInfo", "Thanh toán giao dịch #" + tx.getId());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        // Hash checksum
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String name : fieldNames) {
            String value = vnp_Params.get(name);
            if (value != null && !value.isEmpty()) {
                hashData.append(name).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(name, StandardCharsets.US_ASCII))
                     .append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                if (!name.equals(fieldNames.get(fieldNames.size() - 1))) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String vnp_SecureHash = HmacUtils.hmacSha512Hex(vnp_HashSecret, hashData.toString());
        String paymentUrl = vnp_PayUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;

        response.setMessage("success");
        response.setData(paymentUrl);
        return response;
    }

    @Override
    public Map<String, Object> handleReturn(Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        try {
            String responseCode = params.get("vnp_ResponseCode");
            Long transactionId = Long.parseLong(params.get("vnp_TxnRef"));

            Transaction tx = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch id=" + transactionId));

            boolean success = "00".equals(responseCode);
            tx.setStatus(success ? "SUCCESS" : "FAILED");
            tx.setModifiedDate(new Date());
            tx.setModifiedBy("VNPAY");
            transactionRepository.save(tx);

            result.put("success", success);
            result.put("txnRef", transactionId);
            result.put("amount", params.get("vnp_Amount"));
            result.put("responseCode", responseCode);
            result.put("message", success ? "Thanh toán thành công!" : "Thanh toán thất bại!");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi xử lý callback: " + e.getMessage());
        }

        return result;
    }

}
