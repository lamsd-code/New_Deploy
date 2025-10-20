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

        String otp = otpService.generateRegistrationOtp(target, request.getChannel());
        dispatchOtp(request.getChannel(), target, otp);

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
        }

        boolean validOtp = otpService.verifyRegistrationOtp(target, request.getOtp());
        if (!validOtp) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error("Mã OTP không chính xác hoặc đã hết hạn"));
        }

        try {
            CustomerDTO customer = customerService.registerNewCustomer(request);
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(ex.getMessage()));
        }
    }

    @PostMapping("/login/send-otp")
    public ResponseEntity<?> sendLoginOtp(@RequestBody LoginOtpRequest request) {
        if (request.getChannel() == null) {
            return ResponseEntity.badRequest().body(error("Vui lòng chọn phương thức nhận OTP"));
        }
        if (StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getPassword())) {
            return ResponseEntity.badRequest().body(error("Tên đăng nhập và mật khẩu là bắt buộc"));
        }

        // Thử xác thực với tài khoản nội bộ (User)
        User user = authenticateBackOfficeUser(request);
        if (user != null) {
            String target = resolveTarget(request.getChannel(), user.getEmail(), user.getPhone());
            if (target == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error("Tài khoản chưa cập nhật thông tin nhận OTP"));
            }
            String code = otpService.generateLoginOtp("USER", user.getId(), target, request.getChannel());
            dispatchOtp(request.getChannel(), target, code);
            return ResponseEntity.ok(success("OTP_SENT"));
        }

        // Thử với khách hàng
        Optional<Customer> customerOpt = customerService.findByUsername(request.getUsername());
        if (customerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("Tài khoản hoặc mật khẩu không hợp lệ"));
        }

        Customer customer = customerOpt.get();
        if (customer.getIsActive() != null && customer.getIsActive() == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("Tài khoản đã bị khóa"));
        }
        if (StringUtils.isBlank(customer.getPassword()) || !passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("Tài khoản hoặc mật khẩu không hợp lệ"));
        }

        String target = resolveTarget(request.getChannel(), customer.getEmail(), customer.getPhone());
        if (target == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error("Khách hàng chưa cập nhật thông tin nhận OTP"));
        }
        String code = otpService.generateLoginOtp("CUSTOMER", customer.getId(), target, request.getChannel());
        dispatchOtp(request.getChannel(), target, code);
        return ResponseEntity.ok(success("OTP_SENT"));
    }

    @PostMapping("/login/verify")
    public ResponseEntity<?> verifyLoginOtp(@RequestBody LoginOtpVerifyRequest request) {
        if (StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getOtp())) {
            return ResponseEntity.badRequest().body(error("Thiếu thông tin xác thực"));
        }

        // Ưu tiên kiểm tra tài khoản nội bộ
        User user = userRepository.findOneByUserName(request.getUsername());
        if (user != null) {
            boolean ok = otpService.verifyLoginOtp("USER", user.getId(), request.getOtp());
            return ok ? ResponseEntity.ok(success("OTP_VERIFIED"))
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error("OTP không hợp lệ"));
        }

        Optional<Customer> customerOpt = customerService.findByUsername(request.getUsername());
        if (customerOpt.isPresent()) {
            boolean ok = otpService.verifyLoginOtp("CUSTOMER", customerOpt.get().getId(), request.getOtp());
            return ok ? ResponseEntity.ok(success("OTP_VERIFIED"))
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error("OTP không hợp lệ"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Không tìm thấy tài khoản"));
    }

    private User authenticateBackOfficeUser(LoginOtpRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            if (authentication.isAuthenticated()) {
                return userRepository.findOneByUserName(request.getUsername());
            }
        } catch (AuthenticationException ex) {
            return null;
        }
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
        if (channel == OtpChannel.EMAIL) {
            emailService.sendSimpleMessage(target, "Mã OTP xác thực", message);
        } else {
            smsService.sendOtp(target, message);
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
