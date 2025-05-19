package com.projectsky.auth.controller;

import com.projectsky.auth.dto.JwtAuthenticationDto;
import com.projectsky.auth.dto.RefreshTokenDto;
import com.projectsky.auth.dto.UserCredentialsDto;
import com.projectsky.auth.dto.UserDto;
import com.projectsky.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationDto> signIn(@RequestBody UserCredentialsDto userCredentialsDto) {
        try{
            JwtAuthenticationDto jwtAuthenticationDto = userService.signIn(userCredentialsDto);
            return ResponseEntity.ok(jwtAuthenticationDto);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentication failed " +  e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return userService.refreshToken(refreshTokenDto);
    }

    @PostMapping("/register")
    public String register(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }
}
