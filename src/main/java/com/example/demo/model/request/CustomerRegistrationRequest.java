package com.example.demo.model.request;

import com.example.demo.constant.OtpChannel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRegistrationRequest {
    private String fullname;
    private String phone;
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private String otp;
    private OtpChannel channel;
}
