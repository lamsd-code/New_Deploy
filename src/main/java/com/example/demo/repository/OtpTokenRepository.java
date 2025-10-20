package com.example.demo.repository;

import com.example.demo.constant.OtpPurpose;
import com.example.demo.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByTargetAndPurposeOrderByCreatedAtDesc(String target, OtpPurpose purpose);
    Optional<OtpToken> findTopByOwnerTypeAndOwnerIdAndPurposeOrderByCreatedAtDesc(String ownerType, Long ownerId, OtpPurpose purpose);
}
