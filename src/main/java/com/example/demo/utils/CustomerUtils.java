package com.example.demo.utils;

import com.example.demo.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerUtils {
    public void setCustomerField(Customer customerEntity, Customer customerEntityTmp){
        if(customerEntityTmp.getCreatedBy() != null) customerEntity.setCreatedBy(customerEntityTmp.getCreatedBy());
        if(customerEntityTmp.getCreatedDate() != null) customerEntity.setCreatedDate(customerEntityTmp.getCreatedDate());
        if(customerEntityTmp.getUserEntities() != null) customerEntity.setUserEntities(customerEntityTmp.getUserEntities());
    }
}

