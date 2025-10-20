package com.example.demo.service;

import com.example.demo.constant.OtpChannel;

public interface OtpService {
    String generateRegistrationOtp(String target, OtpChannel channel);
    boolean verifyRegistrationOtp(String target, String otp);
    String generateLoginOtp(String ownerType, Long ownerId, String target, OtpChannel channel);
    boolean verifyLoginOtp(String ownerType, Long ownerId, String otp);
}