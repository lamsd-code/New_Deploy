package com.example.demo.model.request;

import com.example.demo.entity.Base;
import com.example.demo.model.dto.AbstractDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSearchRequest{
    private String fullname;
    private String phone;
    private String email;
    private Long staffId;
}
