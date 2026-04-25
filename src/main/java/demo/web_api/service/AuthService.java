package demo.web_api.service;

import demo.web_api.dto.request.RegisterRequest;
import demo.web_api.dto.request.LoginRequest;
import demo.web_api.dto.response.AuthResponse;
import demo.web_api.dto.response.UserResponse;

public interface AuthService {
    UserResponse signup(RegisterRequest request);
    AuthResponse authenticate(LoginRequest request);    
}