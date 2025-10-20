package com.example.demo.service;


import com.example.demo.model.dto.AssignmentDTO;
import com.example.demo.model.dto.CustomerDTO;
import com.example.demo.model.request.CustomerCreateRequest;
import com.example.demo.model.response.ResponseDTO;
import org.springframework.data.domain.Page;

import java.util.Map;

import java.util.List;
import java.util.Map;

public interface CustomerService {
    List<CustomerDTO> findAll(Map<String, Object> conditions);
    ResponseDTO save(CustomerCreateRequest customerCreateRequest);
    CustomerCreateRequest findOneById(Long id);
    ResponseDTO disableActivity(List<Long> id);
    ResponseDTO findStaffsByCustomerId(Long id);
    ResponseDTO updateAssignmentTable(AssignmentDTO assignmentDTO);
    //Page<CustomerDTO> findAll(Map<String, Object> conditions, int page, int size);
}
