package com.example.demo.controller.web;

import com.example.demo.constant.OtpChannel;
import com.example.demo.entity.Customer;
import com.example.demo.entity.User;
import com.example.demo.model.dto.CustomerDTO;
import com.example.demo.model.request.CustomerRegistrationRequest;
import com.example.demo.model.request.LoginOtpRequest;
import com.example.demo.model.request.LoginOtpVerifyRequest;
import com.example.demo.model.request.RegistrationOtpRequest;
import com.example.demo.service.CustomerService;
import com.example.demo.service.EmailService;
import com.example.demo.service.OtpService;
import com.example.demo.service.SmsService;
import com.example.demo.repository.UserRepository;
import org.apache.commons.lang.StringUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {



    private final EmailService emailService;
    private final SmsService smsService;
    private final OtpService otpService;
    private final CustomerService customerService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthController(EmailService emailService,
                          SmsService smsService,
                          OtpService otpService,
                          CustomerService customerService,
                          UserRepository userRepository,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.otpService = otpService;
        this.customerService = customerService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendRegistrationOtp(@RequestBody RegistrationOtpRequest request) {
        if (request.getChannel() == null) {
            return ResponseEntity.badRequest().body(error("Vui lòng chọn phương thức nhận OTP"));
        }

        String target = resolveTarget(request.getChannel(), request.getEmail(), request.getPhone());
        if (target == null) {
            return ResponseEntity.badRequest().body(error("Thiếu thông tin email hoặc số điện thoại"));
        }

        try {
            String normalizedUsername = StringUtils.trimToNull(request.getUsername());
            String normalizedEmail = StringUtils.trimToNull(request.getEmail());
            String normalizedPhone = StringUtils.trimToNull(request.getPhone());
            customerService.validateRegistrationAvailability(normalizedUsername, normalizedEmail, normalizedPhone);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(ex.getMessage()));
        }



        return ResponseEntity.ok(success("OTP_SENT"));
    }

    @PostMapping("/register/verify")
    public ResponseEntity<?> verifyAndRegister(@RequestBody CustomerRegistrationRequest request) {
        if (request.getChannel() == null) {
            return ResponseEntity.badRequest().body(error("Vui lòng chọn phương thức nhận OTP"));
        }
        if (StringUtils.isBlank(request.getOtp())) {
            return ResponseEntity.badRequest().body(error("Vui lòng nhập mã OTP"));
        }

        String target = resolveTarget(request.getChannel(), request.getEmail(), request.getPhone());
        if (target == null) {
            return ResponseEntity.badRequest().body(error("Thiếu thông tin email hoặc số điện thoại"));

        return null;
    }

    private String resolveTarget(OtpChannel channel, String email, String phone) {
        if (channel == OtpChannel.EMAIL) {
            String normalized = StringUtils.trimToNull(email);
            return normalized;
        }
        if (channel == OtpChannel.PHONE) {
            String normalized = StringUtils.trimToNull(phone);
            return normalized;
        }
        return null;
    }

    private void dispatchOtp(OtpChannel channel, String target, String otp) {
        String message = "Mã OTP của bạn là: " + otp + " (hiệu lực trong 5 phút)";

        }
    }

    private Map<String, String> error(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", message);
        return response;
    }

    private Map<String, String> success(String code) {
        Map<String, String> response = new HashMap<>();
        response.put("status", code);
        return response;
    }
}
