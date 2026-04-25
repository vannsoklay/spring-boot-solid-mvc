package demo.web_api.dto.response;

import lombok.Builder;
import lombok.Getter;

import demo.web_api.dto.response.UserResponse;

@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse user;
}