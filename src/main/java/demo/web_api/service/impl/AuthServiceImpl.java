package demo.web_api.service.impl;

import demo.web_api.dto.request.LoginRequest;
import demo.web_api.dto.request.RegisterRequest;
import demo.web_api.dto.response.AuthResponse;
import demo.web_api.dto.response.UserResponse;
import demo.web_api.exception.BadRequestException;
import demo.web_api.exception.ResourceNotFoundException;
import demo.web_api.model.entity.UserEntity;
import demo.web_api.repository.UserRepository;
import demo.web_api.security.JwtService;
import demo.web_api.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public UserResponse signup(RegisterRequest input) {

        if (userRepository.existsByUsername(input.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        UserEntity user = UserEntity.builder()
                .username(input.getUsername())
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .enabled(true)
                .build();

        UserEntity saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail()
        );
    }

    @Override
    public AuthResponse authenticate(LoginRequest input) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getUsername(),
                            input.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new BadRequestException("Invalid username or password");
        }

        UserEntity user = userRepository.findByUsername(input.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", "username", input.getUsername())
                );

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                ))
                .build();
    }
}