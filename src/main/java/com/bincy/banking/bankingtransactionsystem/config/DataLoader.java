package com.bincy.banking.bankingtransactionsystem.config;





import com.bincy.banking.bankingtransactionsystem.entity.AppUser;
import com.bincy.banking.bankingtransactionsystem.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public DataLoader(
            UserRepository repository,
            PasswordEncoder encoder) {

        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public void run(String @NonNull ... args) {

        if (repository.findByUsername("admin").isEmpty()) {

            AppUser user = AppUser.builder()
                    .username("admin")
                    .password(encoder.encode("password"))
                    .role("ADMIN")
                    .build();

            repository.save(user);
        }
    }
}
