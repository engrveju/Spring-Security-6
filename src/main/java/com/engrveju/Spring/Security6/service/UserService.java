package com.engrveju.Spring.Security6.service;

import com.engrveju.Spring.Security6.pojos.AuthenticationRequest;
import com.engrveju.Spring.Security6.pojos.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> userLogin(AuthenticationRequest loginRequest);

    ResponseEntity<?> registerUser(RegisterRequest signUpRequest);

    ResponseEntity<?> confirmAccount(String token);

    ResponseEntity<?> resendConfirmationMail(String email);
}
