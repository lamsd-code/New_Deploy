package com.example.demo.service.impl;

import com.example.demo.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Override
    public void sendOtp(String phoneNumber, String message) {
        logger.info("[SMS-OTP] to {} => {}", phoneNumber, message);
    }
}
