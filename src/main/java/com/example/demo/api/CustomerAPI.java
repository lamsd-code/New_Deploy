package com.example.demo.api;

import com.example.demo.model.dto.AssignmentDTO;
import com.example.demo.model.dto.CustomerDTO;
import com.example.demo.model.request.CustomerCreateRequest;
import com.example.demo.model.request.TransactionCreateRequest;
import com.example.demo.model.response.ResponseDTO;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@Transactional
@RequestMapping("/api/customer")
public class CustomerAPI {
    @Autowired
    private CustomerService customerService;
    @PreAuthorize("hasAnyAuthority('STAFF','MANAGER','ADMIN')")
    @PostMapping
    public ResponseDTO addOrUpdateCustomer(@RequestBody CustomerCreateRequest customerCreateRequest){
        return customerService.save(customerCreateRequest);
    }
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    @PostMapping("/{ids}")
    public ResponseDTO disableActivity(@PathVariable List<Long> ids){
        return customerService.disableActivity(ids);
    }
    @PreAuthorize("hasAnyAuthority('MANAGER','STAFF','ADMIN')")
    @GetMapping("/{id}/staffs")
    public ResponseDTO loadStaffs(@PathVariable Long id){
        ResponseDTO responseDTO = customerService.findStaffsByCustomerId(id);
        return responseDTO;
    }
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    @PostMapping("/assignment")
    public ResponseDTO updateAssignmentCustomer(@RequestBody AssignmentDTO assignmentDTO){
        return customerService.updateAssignmentTable(assignmentDTO);
    }
}
