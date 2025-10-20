package com.example.demo.model.request;

import com.example.demo.constant.OtpChannel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginOtpRequest {
    private String username;
    private String password;
    private OtpChannel channel;
}
