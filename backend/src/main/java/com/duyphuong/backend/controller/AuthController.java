package com.duyphuong.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.duyphuong.backend.domain.User;
import com.duyphuong.backend.domain.request.ReqLoginDTO;
import com.duyphuong.backend.domain.response.ResLoginDTO;
import com.duyphuong.backend.service.UserService;
import com.duyphuong.backend.util.SecurityUtil;
import com.duyphuong.backend.util.annotation.ApiMessage;
import com.duyphuong.backend.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
public class AuthController {
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;

        @Value("${myapp.jwt.access-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                        SecurityUtil securityUtil,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
        }

        @PostMapping("/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDto) {
                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDto.getUsername(), loginDto.getPassword());
                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                // Đưa thông tin user đã xác thực vào SecurityContext hiện tại
                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResLoginDTO res = new ResLoginDTO();

                // xử lý dữ liệu trả về sau khi login
                User currentUserDB = this.userService.handleGetUserByUsername(loginDto.getUsername());
                if (currentUserDB != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                        currentUserDB.getId(),
                                        currentUserDB.getEmail(),
                                        currentUserDB.getName());
                        res.setUser(userLogin);
                }
                // create a token

                String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());

                // Gán access token cho response
                res.setAccessToken(access_token);
                // create refresh token
                String refresh_token = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);
                // update refresh token user
                this.userService.updateUserToken(refresh_token, loginDto.getUsername());
                // set cookies
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @GetMapping("/auth/account")
        @ApiMessage("fetch account")
        public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                User currentUserDB = this.userService.handleGetUserByUsername(email);
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
                ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
                if (currentUserDB != null) {
                        userLogin.setId(currentUserDB.getId());
                        userLogin.setEmail(currentUserDB.getEmail());
                        userLogin.setName(currentUserDB.getName());
                        userGetAccount.setUser(userLogin);
                }
                return ResponseEntity.ok().body(userGetAccount);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("Get User by refresh token")
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
                        throws IdInvalidException {
                if (refresh_token.equals("abc")) {
                        throw new IdInvalidException("bạn không có refresh token ở cookie");
                }
                // check valid
                Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
                String email = decodedToken.getSubject();

                // check user by token + email
                User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
                if (currentUser == null) {
                        throw new IdInvalidException("Refresh Token không hợp lệ");
                }

                // issue new token/set refresh token as cookies
                ResLoginDTO res = new ResLoginDTO();
                User currenttUserDB = this.userService.handleGetUserByUsername(email);
                if (currenttUserDB != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                        currenttUserDB.getId(),
                                        currenttUserDB.getEmail(),
                                        currenttUserDB.getName());

                        res.setUser(userLogin);
                }

                // create access token
                String access_token = this.securityUtil.createAccessToken(email, res.getUser());
                res.setAccessToken(access_token);

                // create refresh token
                String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

                // update user
                this.userService.updateUserToken(new_refresh_token, email);

                // set cookies
                ResponseCookie resCookie = ResponseCookie
                                .from("refresh_token", new_refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                                .body(res);
        }

        @PostMapping("/auth/logout")
        @ApiMessage("Logout User")
        public ResponseEntity<Void> logout() throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                if (email.equals("")) {
                        throw new IdInvalidException("Access Token không hợp lệ");
                }

                // update refresh token == null
                this.userService.updateUserToken(null, email);

                // remove refresh tooken cookie
                ResponseCookie deleteSpringCookie = ResponseCookie
                                .from("refresh_token", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                                .body(null);
        }

}
