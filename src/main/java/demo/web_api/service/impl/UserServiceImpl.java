package demo.web_api.service.impl;

import demo.web_api.dto.response.UserResponse;
import demo.web_api.exception.ResourceNotFoundException;
import demo.web_api.exception.UnauthorizedException;
import demo.web_api.model.entity.UserEntity;
import demo.web_api.repository.UserRepository;
import demo.web_api.security.JwtService;
import demo.web_api.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public UserResponse getCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        String username = auth.getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail());
    }

    @Override
    public void logout(String token) {

        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Token is required");
        }

        try {
            String jti = jwtService.extractJti(token);
            jwtService.blacklistToken(jti);
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid token");
        }
    }
}