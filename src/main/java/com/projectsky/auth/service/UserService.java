package com.projectsky.auth.service;

import com.projectsky.auth.dto.*;
import org.springframework.data.crossstore.ChangeSetPersister;

import javax.naming.AuthenticationException;

public interface UserService {

    JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto);

    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception;

    JwtAuthenticationDto completeRegistration(RegisterPasswordRequest request);

    void initRegistration(RegisterInitRequest request);

    void confirmCode(ConfirmCodeRequest request);


}
