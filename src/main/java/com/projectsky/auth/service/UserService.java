package com.projectsky.auth.service;

import com.projectsky.auth.dto.JwtAuthenticationDto;
import com.projectsky.auth.dto.RefreshTokenDto;
import com.projectsky.auth.dto.UserCredentialsDto;
import com.projectsky.auth.dto.UserDto;
import org.springframework.data.crossstore.ChangeSetPersister;

import javax.naming.AuthenticationException;

public interface UserService {

    JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException;

    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception;

    String addUser(UserDto userDto);

    UserDto getUserById(String id) throws ChangeSetPersister.NotFoundException;

    UserDto getUserByEmail(String email) throws ChangeSetPersister.NotFoundException;
}
