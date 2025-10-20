package com.example.demo.repository.custom;

import com.example.demo.entity.Customer;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerRepositoryCustom {
    List<Customer> findAll(Map<String, Object> conditions);
    //Page<Customer> findAll(Map<String, Object> conditions, Pageable pageable);
}

