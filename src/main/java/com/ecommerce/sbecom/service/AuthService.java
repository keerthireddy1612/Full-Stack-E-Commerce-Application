package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.payload.AuthenticationResult;
import com.ecommerce.sbecom.payload.UserResponse;
import com.ecommerce.sbecom.security.request.LoginRequest;
import com.ecommerce.sbecom.security.request.SignupRequest;
import com.ecommerce.sbecom.security.response.UserInfoResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;


public interface AuthService {
    AuthenticationResult login(LoginRequest loginRequest);

    ResponseEntity<?> register(@Valid SignupRequest signUpRequest);

    UserInfoResponse getCurrentUserDetails(Authentication authentication);

    ResponseCookie logOutUser();

    UserResponse getAllSellers(Pageable pageDetails);
}
