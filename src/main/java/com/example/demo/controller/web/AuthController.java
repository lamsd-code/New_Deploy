package com.example.demo.controller.web;

import com.example.demo.model.dto.UserDTO;
import com.example.demo.service.EmailService;
import com.example.demo.service.OtpService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserService userService;

    @PostMapping("/register/send-otp")
    public String sendOtp(@RequestParam String email) {
        String otp = otpService.generateOtp(email);
        emailService.sendSimpleMessage(email, "Your OTP", "Your verification code: " + otp);
        return "OTP_SENT";
    }

    @PostMapping("/register/verify")
    public String verifyAndRegister(@RequestParam String email, @RequestParam String otp, @RequestBody UserDTO user) {
        if (!otpService.verifyOtp(email, otp)) {
            return "OTP_INVALID";
        }
        // TODO: map UserDTO -> entity và lưu; sử dụng userService
        userService.createUser(user); // implement createUser trong UserServiceImpl
        return "REGISTERED";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        // delegate to Spring Security normally; here return placeholder
        return "IMPLEMENT_LOGIN_WITH_SPRING_SECURITY";
    }
}