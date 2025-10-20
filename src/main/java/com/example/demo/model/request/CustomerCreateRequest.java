package com.example.demo.model.request;


import com.example.demo.model.dto.AbstractDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerCreateRequest extends AbstractDTO {
    private String fullname;
    private String phone;
    private String email;
    private String companyname;
    private String demand;
    private String status;
}
