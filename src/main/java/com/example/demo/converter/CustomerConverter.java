package com.example.demo.converter;


import com.example.demo.entity.Customer;
import com.example.demo.model.dto.CustomerDTO;
import com.example.demo.model.request.CustomerCreateRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerConverter {
    @Autowired
    private ModelMapper modelMapper;
    public CustomerDTO toCustomerDTO(Customer customerEntity){
        CustomerDTO customerDTO = modelMapper.map(customerEntity, CustomerDTO.class);
        return customerDTO;
    }
    public Customer toCustomerEntity(CustomerCreateRequest customerCreateRequest){
        Customer customerEntity = modelMapper.map(customerCreateRequest, Customer.class);
        
        customerEntity.setFullname(customerCreateRequest.getFullname());
        customerEntity.setPhone(customerCreateRequest.getPhone());
        customerEntity.setEmail(customerCreateRequest.getEmail());
        //customerEntity.setCompanyname(customerCreateRequest.getCompanyname());
        customerEntity.setDemand(customerCreateRequest.getDemand());
        // ⚙️ Nếu khách không nhập companyname hoặc status → gán mặc định
        if (customerCreateRequest.getCompanyname() == null || customerCreateRequest.getCompanyname().trim().isEmpty()) {
        	customerEntity.setCompanyname("Chưa cập nhật");
        } else {
        	customerEntity.setCompanyname(customerCreateRequest.getCompanyname());
        }

        if (customerCreateRequest.getStatus() == null || customerCreateRequest.getStatus().trim().isEmpty()) {
        	customerEntity.setStatus("Chưa xử lý");
        } else {
        	customerEntity.setStatus(customerCreateRequest.getStatus());
        }

        // ⚙️ Mặc định là khách hàng hoạt động
        customerEntity.setIsActive(1);
        return customerEntity;
    }
    public CustomerCreateRequest toCustomerCreateRequest(Customer customerEntity){
        CustomerCreateRequest customerCreateRequest = modelMapper.map(customerEntity, CustomerCreateRequest.class);
        return customerCreateRequest;
    }
}

