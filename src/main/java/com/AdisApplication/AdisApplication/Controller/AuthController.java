package com.AdisApplication.AdisApplication.Controller;


import com.AdisApplication.AdisApplication.dto.AuthResponse;
import com.AdisApplication.AdisApplication.dto.LoginRequest;
import com.AdisApplication.AdisApplication.entity.User;
import com.AdisApplication.AdisApplication.service.AdminService;
import com.AdisApplication.AdisApplication.service.JwtService;
import com.AdisApplication.AdisApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "file://"})
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String role = loginRequest.getRole();
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            // Validate credentials based on role
            if ("ADMIN".equals(role)) {
                boolean isValidAdmin = adminService.validateAdmin(username, password);
                if (!isValidAdmin) {
                    return ResponseEntity.badRequest().body("Invalid admin credentials");
                }
            } else if ("USER".equals(role)) {
                // For users, we need to validate against the user table
                User user = userService.findByUsername(username).orElse(null);
                if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
                    return ResponseEntity.badRequest().body("Invalid user credentials");
                }

                // Update user login time and session expiry
                userService.updateLoginTime(username);
            } else {
                return ResponseEntity.badRequest().body("Invalid role specified");
            }

            // Generate JWT token - FIXED: Use the JwtService to generate token
            String token = jwtService.generateToken(username, role);

            // Prepare response
            AuthResponse response = new AuthResponse();
            response.setSuccess(true);
            response.setToken(token);
            response.setUsername(username);
            response.setRole(role);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        // JWT is stateless, so logout is handled on the client side by discarding the token
        return ResponseEntity.ok().body("Logged out successfully");
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkServer() {
        return ResponseEntity.ok().body("Server is running");
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.isTokenValid(token) && !jwtService.isTokenExpired(token)) {
                return ResponseEntity.ok().body("Token is valid");
            }
        }
        return ResponseEntity.status(401).body("Invalid token");
    }

    @PostMapping("/extend")
    public ResponseEntity<?> extendSession(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.isTokenValid(token) && !jwtService.isTokenExpired(token)) {
                String username = jwtService.extractUsername(token);
                String role = jwtService.extractRole(token);

                if ("USER".equals(role)) {
                    User user = userService.findByUsername(username).orElse(null);
                    if (user != null) {
                        userService.extendSession(user.getId());

                        // Generate new token with extended expiration
                        String newToken = jwtService.generateToken(username, role);

                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("token", newToken);
                        response.put("message", "Session extended successfully");

                        return ResponseEntity.ok(response);
                    }
                } else {
                    return ResponseEntity.ok().body("Admin sessions don't require extension");
                }
            }
        }
        return ResponseEntity.status(401).body("Invalid token");
    }
}