package kr.moonwalk.moonwalk_api.repository;

import java.util.Optional;
import kr.moonwalk.moonwalk_api.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    boolean existsByName(String name);
}
