package com.projectsky.auth.service;

import com.projectsky.auth.dto.JwtAuthenticationDto;
import com.projectsky.auth.dto.RefreshTokenDto;
import com.projectsky.auth.dto.UserCredentialsDto;
import com.projectsky.auth.dto.UserDto;
import com.projectsky.auth.mapper.UserMapper;
import com.projectsky.auth.model.User;
import com.projectsky.auth.repository.UserRepository;
import com.projectsky.auth.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           JwtService jwtService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        User user = findByCredentials(userCredentialsDto);
        return jwtService.generateAuthToken(user.getEmail());
    }

    @Override
    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.refreshToken();
        if(refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }
        throw new AuthenticationException("Invalid refresh token");
    }

    @Override
    public String addUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User added successfully";
    }

    @Override
    @Transactional
    public UserDto getUserById(String id) throws ChangeSetPersister.NotFoundException {
        return userMapper.toDto(userRepository.findByUserId(UUID.fromString(id))
                .orElseThrow(ChangeSetPersister.NotFoundException::new));
    }

    @Override
    @Transactional
    public UserDto getUserByEmail(String email) throws ChangeSetPersister.NotFoundException {
        return userMapper.toDto(userRepository.findByEmail(email)
                .orElseThrow(ChangeSetPersister.NotFoundException::new));
    }

    private User findByCredentials(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        Optional<User> optUser = userRepository.findByEmail(userCredentialsDto.email());
        if (optUser.isPresent()) {
            User user = optUser.get();
            if(passwordEncoder.matches(userCredentialsDto.password(), user.getPassword())) {
                return user;
            }
        }
        throw new AuthenticationException("Email or password is incorrect");
    }

    private User findByEmail(String email) throws Exception {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception(String.format("User with email %s not found", email)));
    }
}
