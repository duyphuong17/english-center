package com.duyphuong.backend.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtil {
    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    @Value("${myapp.jwt.base64-secret}")
    private String jwtKey;

    @Value("${myapp.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    public String createAccessToken(Authentication authentication) {

        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // ===== TẠO PAYLOAD (CLAIMS) CỦA JWT =====
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("user", authentication)
                .build();

        // ===== TẠO HEADER CỦA JWT bằng thuật toán HS512
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        // KÝ + ENCODE JWT
        // Trả về chuỗi JWT dạng: header.payload.signature
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}
