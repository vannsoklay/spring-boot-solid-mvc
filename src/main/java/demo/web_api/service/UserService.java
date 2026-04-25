package demo.web_api.service;

import demo.web_api.dto.response.UserResponse;

public interface UserService {
    UserResponse getCurrentUser();
    void logout(String token);
}