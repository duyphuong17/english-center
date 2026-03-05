package com.duyphuong.backend.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import com.duyphuong.backend.domain.response.ResLoginDTO;
import com.nimbusds.jose.util.Base64;

@Service
public class SecurityUtil {
    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    @Value("${myapp.jwt.base64-secret}")
    private String jwtKey;

    @Value("${myapp.jwt.refresh-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${myapp.jwt.access-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public String createAccessToken(String email, ResLoginDTO.UserLogin dto) {

        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);
        // hardcode permission (for testing)
        List<String> listAuthority = new ArrayList<String>();

        listAuthority.add("ROLE_USER_CREATE");
        listAuthority.add("ROLE_USER_UPDATE");

        // ===== TẠO PAYLOAD (CLAIMS) CỦA JWT =====
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", dto)
                .claim("permission", listAuthority)
                .build();

        // ===== TẠO HEADER CỦA JWT bằng thuật toán HS512
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        // KÝ + ENCODE JWT
        // Trả về chuỗi JWT dạng: header.payload.signature
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String createRefreshToken(String email, ResLoginDTO dto) {

        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

        // ===== TẠO PAYLOAD (CLAIMS) CỦA JWT =====
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", dto.getUser())
                .build();

        // ===== TẠO HEADER CỦA JWT bằng thuật toán HS512
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        // KÝ + ENCODE JWT
        // Trả về chuỗi JWT dạng: header.payload.signature
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    // Phương thức lấy username (login) của user hiện tại
    public static Optional<String> getCurrentUserLogin() {
        // Lấy SecurityContext hiện tại (lưu thông tin bảo mật của request)
        SecurityContext securityContext = SecurityContextHolder.getContext();

        // Trích xuất principal từ Authentication và bọc trong Optional
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    // Phương thức private dùng để lấy thông tin principal từ Authentication
    private static String extractPrincipal(Authentication authentication) {
        // Nếu chưa có authentication (user chưa đăng nhập)
        if (authentication == null) {
            return null;
            // Trường hợp principal là UserDetails (thường dùng với login form truyền thống)
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            // Trả về username
            return springSecurityUser.getUsername();
            // Trường hợp principal là Jwt (thường dùng với OAuth2 / JWT)
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            // Trả về subject của JWT (thường là userId hoặc username)
            return jwt.getSubject();
            // Trường hợp principal chỉ là String (ví dụ: "anonymousUser")
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }

        // Các trường hợp khác không xác định
        return null;
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length,
                JWT_ALGORITHM.getName());
    }

    public Jwt checkValidRefreshToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();

        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            System.out.println(">>> Refresh Token error: " + e.getMessage());
            throw e;
        }
    }

}
