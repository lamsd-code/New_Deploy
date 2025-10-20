package com.example.demo.service.impl;

import com.example.demo.service.OtpService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService {

    private static class OtpEntry {
        String code;
        Instant expiresAt;
    }

    private final Map<String, OtpEntry> store = new ConcurrentHashMap<>();
    private final Random rnd = new Random();

    @Override
    public String generateOtp(String key) {
        String code = String.format("%06d", rnd.nextInt(1_000_000));
        OtpEntry e = new OtpEntry();
        e.code = code;
        e.expiresAt = Instant.now().plusSeconds(5 * 60); // 5 phút
        store.put(key, e);
        return code;
    }

    @Override
    public boolean verifyOtp(String key, String otp) {
        OtpEntry e = store.get(key);
        if (e == null) return false;
        if (Instant.now().isAfter(e.expiresAt)) {
            store.remove(key);
            return false;
        }
        boolean ok = e.code.equals(otp);
        if (ok) store.remove(key);
        return ok;
    }
}