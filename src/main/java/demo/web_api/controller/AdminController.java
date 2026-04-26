package demo.web_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "APIs for administrative operations (ADMIN only)")
public class AdminController {

    @GetMapping("/dashboard")
    @Operation(
            summary = "Admin dashboard",
            description = "Returns admin dashboard data - requires ADMIN role",
            security = @SecurityRequirement(name = "JWT Auth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> dashboard() {
        return ResponseEntity.ok(Map.of(
                "message", "Welcome to Admin Dashboard",
                "totalUsers", 0,
                "totalTransactions", 0
        ));
    }

    @GetMapping("/users")
    @Operation(
            summary = "List all users",
            description = "Returns list of all users - requires ADMIN role",
            security = @SecurityRequirement(name = "JWT Auth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        return ResponseEntity.ok(Map.of(
                "message", "List of all users endpoint",
                "users", new Object[]{}
        ));
    }
}