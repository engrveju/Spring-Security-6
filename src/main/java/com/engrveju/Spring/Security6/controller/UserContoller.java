package com.engrveju.Spring.Security6.controller;

import com.engrveju.Spring.Security6.pojos.AuthenticationRequest;
import com.engrveju.Spring.Security6.pojos.RegisterRequest;
import com.engrveju.Spring.Security6.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("api/v1/auth")
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserContoller {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@Valid @RequestBody AuthenticationRequest request){
        return userService.userLogin(request);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        return userService.registerUser(request);
    }

    @GetMapping("/confirm/{token}")
    public ResponseEntity<?> confirm(@Valid @PathVariable String token){
        return userService.confirmAccount(token);
    }

    @PostMapping("/resendConfirmationEmail")
    public ResponseEntity<?> resendConfirmationMail(@RequestParam  String email){
        return userService.resendConfirmationMail(email);
    }
}
