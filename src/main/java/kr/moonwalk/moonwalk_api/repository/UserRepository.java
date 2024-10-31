package kr.moonwalk.moonwalk_api.repository;

import java.util.Optional;
import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.domain.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByRole(Role role);
}
