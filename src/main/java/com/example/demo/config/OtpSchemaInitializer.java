package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class OtpSchemaInitializer {

    private static final Logger logger = LoggerFactory.getLogger(OtpSchemaInitializer.class);

    private final JdbcTemplate jdbcTemplate;

    public OtpSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void ensureSchema() {
        try {
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS otp_token (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    otp_code VARCHAR(10) NOT NULL,
                    target VARCHAR(255) NOT NULL,
                    channel VARCHAR(20) NOT NULL,
                    purpose VARCHAR(20) NOT NULL,
                    owner_type VARCHAR(50),
                    owner_id BIGINT,
                    expires_at DATETIME(6) NOT NULL,
                    verified TINYINT(1) NOT NULL DEFAULT 0,
                    created_at DATETIME(6) NOT NULL,
                    updated_at DATETIME(6) NOT NULL,
                    INDEX idx_otp_target_purpose (target, purpose),
                    INDEX idx_otp_owner_purpose (owner_type, owner_id, purpose)
                )
            """);
        } catch (DataAccessException ex) {
            logger.error("Không thể khởi tạo bảng otp_token", ex);
            throw ex;
        }
    }
}
