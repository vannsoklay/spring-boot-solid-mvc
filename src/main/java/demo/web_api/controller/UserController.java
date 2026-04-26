package demo.web_api.controller;

import demo.web_api.common.response.ApiResponse;
import demo.web_api.dto.response.UserResponse;
import demo.web_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user operations and profile management")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile of the currently authenticated user",
            security = @SecurityRequirement(name = "JWT Auth")
    )
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/me/roles")
    @Operation(
            summary = "Get current user roles",
            description = "Returns the authorities/roles of the currently authenticated user for debugging",
            security = @SecurityRequirement(name = "JWT Auth")
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUserRoles() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return ResponseEntity.status(401).body("No authentication found");
        }
        return ResponseEntity.ok(auth.getAuthorities());
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout user",
            description = "Invalidates the current JWT token to logout the user",
            security = @SecurityRequirement(name = "JWT Auth")
    )
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
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