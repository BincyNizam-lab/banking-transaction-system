package com.bincy.banking.transaction.controller;


import com.bincy.banking.transaction.dto.AuthRequest;
import com.bincy.banking.transaction.entity.AppUser;
import com.bincy.banking.transaction.repository.UserRepository;
import com.bincy.banking.transaction.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public AuthController(
            JwtUtil jwtUtil,
            UserRepository repository,
            PasswordEncoder encoder) {

        this.jwtUtil = jwtUtil;
        this.repository = repository;
        this.encoder = encoder;
    }

    @PostMapping("/login")
    public String login(
            @RequestBody AuthRequest request) {

        AppUser user = repository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid credentials"));

        if (!encoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid credentials");
        }

        return jwtUtil.generateToken(user.getUsername(),user.getRole());
    }
}
