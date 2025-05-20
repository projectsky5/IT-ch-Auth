package com.projectsky.auth.controller;

import com.projectsky.auth.dto.*;
import com.projectsky.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationDto> signIn(
            @RequestBody UserCredentialsDto userCredentialsDto
    ) {
        return ResponseEntity.ok(userService.signIn(userCredentialsDto));
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh(
            @RequestBody RefreshTokenDto refreshTokenDto
    ) throws Exception {
        return userService.refreshToken(refreshTokenDto);
    }

    @PostMapping("/register/init")
    public ResponseEntity<Void> init(
            @RequestBody RegisterInitRequest request
    ){
        userService.initRegistration(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(
            @RequestBody ConfirmCodeRequest request
    ) {
        userService.confirmCode(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register/complete")
    public ResponseEntity<JwtAuthenticationDto> complete(
            @RequestBody RegisterPasswordRequest request
    ) {
        return ResponseEntity.ok(userService.completeRegistration(request));
    }
}
