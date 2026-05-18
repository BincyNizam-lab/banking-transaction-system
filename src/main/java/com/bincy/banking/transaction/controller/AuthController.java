package com.bincy.banking.transaction.controller;


import com.bincy.banking.transaction.dto.AuthRequest;
import com.bincy.banking.transaction.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {

        // Temporary hardcoded validation
        if ("admin".equals(request.getUsername())
                && "password".equals(request.getPassword())) {

            return jwtUtil.generateToken(request.getUsername());
        }

        throw new RuntimeException("Invalid credentials");
    }
}
