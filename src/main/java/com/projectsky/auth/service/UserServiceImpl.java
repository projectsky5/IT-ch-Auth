package com.projectsky.auth.service;

import com.projectsky.auth.client.BackendClient;
import com.projectsky.auth.dto.*;
import com.projectsky.auth.exception.IncorrectConfirmationCodeException;
import com.projectsky.auth.exception.InvalidCredentialsException;
import com.projectsky.auth.exception.UserNotConfirmedException;
import com.projectsky.auth.exception.UserNotFoundException;
import com.projectsky.auth.model.User;
import com.projectsky.auth.repository.UserRepository;
import com.projectsky.auth.security.jwt.JwtService;
import com.projectsky.auth.util.RoleResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final BackendClient backendClient;
    private final RoleResolver roleResolver;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder,
                           BackendClient backendClient,
                           RoleResolver roleResolver,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.backendClient = backendClient;
        this.roleResolver = roleResolver;
        this.emailService = emailService;
    }

    @Override
    public JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto) throws UserNotConfirmedException {
        User user = findByCredentials(userCredentialsDto);

        if(!user.isConfirmed()){
            throw new UserNotConfirmedException("User is not confirmed");
        }
        return jwtService.generateAuthToken(user.getEmail());
    }

    @Override
    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.refreshToken();
        if(refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }
        throw new InvalidCredentialsException("Invalid refresh token");
    }

    @Override
    public JwtAuthenticationDto completeRegistration(RegisterPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User with that email not found"));

        if (!user.isConfirmed()) throw new IllegalStateException("User is not confirmed");

        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        String role = roleResolver.resolveRoleFromEmail(user.getEmail());

        backendClient.createUserInBackend(new UserCreateRequest(
                user.getEmail(),
                user.getFullName(),
                role
        ));

        return jwtService.generateAuthToken(user.getEmail());
    }

    @Override
    public void initRegistration(RegisterInitRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalStateException("User already exists");
        }

        String confirmationCode = String.format("%06d", new Random().nextInt(999999));

        User user = new User();
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setConfirmed(false);
        user.setConfirmationCode(confirmationCode);
        user.setUserId(UUID.randomUUID());

        userRepository.save(user);
        emailService.sendConfirmationCode(user.getEmail(), confirmationCode);
    }

    @Override
    public void confirmCode(ConfirmCodeRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User with email ... not found"));

        if(user.isConfirmed()) {
            throw new IllegalStateException("User already confirmed");
        }

        if(!user.getConfirmationCode().equals(request.code())){
            throw new IncorrectConfirmationCodeException("Incorrect confirmation code");
        }

        user.setConfirmed(true);
        user.setConfirmationCode(null);
        userRepository.save(user);

    }

    private User findByCredentials(UserCredentialsDto userCredentialsDto) throws UserNotConfirmedException {
        Optional<User> optUser = userRepository.findByEmail(userCredentialsDto.email());
        if (optUser.isPresent()) {
            User user = optUser.get();
            if(passwordEncoder.matches(userCredentialsDto.password(), user.getPassword())) {
                return user;
            }
        }
        throw new UserNotConfirmedException("Email or password is incorrect");
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email %s not found".formatted(email)));
    }


}
