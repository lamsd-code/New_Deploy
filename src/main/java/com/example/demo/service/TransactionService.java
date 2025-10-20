package com.example.demo.service;

import com.example.demo.entity.Transaction;
import com.example.demo.model.dto.TransactionDTO;
import com.example.demo.model.request.TransactionCreateRequest;
import com.example.demo.model.response.ResponseDTO;

import java.util.List;

public interface TransactionService {
    ResponseDTO save(TransactionCreateRequest transactionCreateRequest, Long staffId);
    List<TransactionDTO> findAllByCodeAndCustomer(String code, Long id);
    ResponseDTO findById(Long id);
}
