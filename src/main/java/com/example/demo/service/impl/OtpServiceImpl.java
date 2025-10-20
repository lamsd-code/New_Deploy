package com.example.demo.service.impl;

import com.example.demo.constant.OtpChannel;
import com.example.demo.constant.OtpPurpose;
import com.example.demo.entity.OtpToken;
import com.example.demo.repository.OtpTokenRepository;
import com.example.demo.service.OtpService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final Random random = new SecureRandom();

    public OtpServiceImpl(OtpTokenRepository otpTokenRepository) {
        this.otpTokenRepository = otpTokenRepository;
    }

    @Override
    public String generateRegistrationOtp(String target, OtpChannel channel) {
        return persistToken(target, channel, OtpPurpose.REGISTRATION, null, null);
    }

    @Override
    public boolean verifyRegistrationOtp(String target, String otp) {
        Optional<OtpToken> tokenOpt = otpTokenRepository.findTopByTargetAndPurposeOrderByCreatedAtDesc(target, OtpPurpose.REGISTRATION);
        return verifyAndConsume(tokenOpt, otp);
    }

    @Override
    public String generateLoginOtp(String ownerType, Long ownerId, String target, OtpChannel channel) {
        return persistToken(target, channel, OtpPurpose.LOGIN, ownerType, ownerId);
    }

    @Override
    public boolean verifyLoginOtp(String ownerType, Long ownerId, String otp) {
        Optional<OtpToken> tokenOpt = otpTokenRepository.findTopByOwnerTypeAndOwnerIdAndPurposeOrderByCreatedAtDesc(ownerType, ownerId, OtpPurpose.LOGIN);
        return verifyAndConsume(tokenOpt, otp);
    }

    private String persistToken(String target, OtpChannel channel, OtpPurpose purpose, String ownerType, Long ownerId) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        OtpToken token = new OtpToken();
        token.setTarget(target);
        token.setChannel(channel);
        token.setPurpose(purpose);
        token.setOwnerType(ownerType);
        token.setOwnerId(ownerId);
        token.setCode(code);
        token.setExpiresAt(Instant.now().plusSeconds(5 * 60));
        token.setVerified(Boolean.FALSE);
        otpTokenRepository.save(token);
        return code;
    }

    private boolean verifyAndConsume(Optional<OtpToken> tokenOpt, String otp) {
        if (tokenOpt.isEmpty()) {
            return false;
        }
        OtpToken token = tokenOpt.get();
        if (Boolean.TRUE.equals(token.getVerified())) {
            return false;
        }
        if (Instant.now().isAfter(token.getExpiresAt())) {
            otpTokenRepository.delete(token);
            return false;
        }
        boolean matched = token.getCode().equals(otp);
        if (matched) {
            token.setVerified(Boolean.TRUE);
            otpTokenRepository.save(token);
        }
        return matched;
    }
}