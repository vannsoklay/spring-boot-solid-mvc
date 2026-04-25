package demo.web_api.controller;

import demo.web_api.common.response.ApiResponse;
import demo.web_api.dto.response.UserResponse;
import demo.web_api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.builder()
                            .status(400)
                            .message("Missing or invalid Authorization header")
                            .build()
            );
        }

        String token = authHeader.substring(7);
        userService.logout(token);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Logged out successfully")
                        .build()
        );
    }
}