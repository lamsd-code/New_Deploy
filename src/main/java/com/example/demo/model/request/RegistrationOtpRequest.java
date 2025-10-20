package com.example.demo.model.request;

import com.example.demo.constant.OtpChannel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationOtpRequest {
    private OtpChannel channel;
    private String email;
    private String phone;
    private String username;
}
