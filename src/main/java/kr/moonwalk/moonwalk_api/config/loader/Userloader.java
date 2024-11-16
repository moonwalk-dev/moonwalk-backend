package kr.moonwalk.moonwalk_api.config.loader;

import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.domain.User.Role;
import kr.moonwalk.moonwalk_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(1)
public class Userloader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        boolean adminExists = userRepository.existsByRole(Role.ROLE_ADMIN);

        if (!adminExists) {
            User adminUser = new User(
                "moonwalk",
                passwordEncoder.encode("admin1234"),
                Role.ROLE_ADMIN
            );

            userRepository.save(adminUser);
        }
    }
}
