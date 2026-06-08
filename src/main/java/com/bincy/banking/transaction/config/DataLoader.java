package com.bincy.banking.transaction.config;





import com.bincy.banking.transaction.entity.AppUser;
import com.bincy.banking.transaction.repository.UserRepository;
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
    public void run(String... args) {

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
