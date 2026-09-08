package com.bincy.banking.bankingtransactionsystem.controller;


import com.bincy.banking.bankingtransactionsystem.dto.AuthRequest;
import com.bincy.banking.bankingtransactionsystem.entity.AppUser;
import com.bincy.banking.bankingtransactionsystem.repository.UserRepository;
import com.bincy.banking.bankingtransactionsystem.security.JwtUtil;
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
