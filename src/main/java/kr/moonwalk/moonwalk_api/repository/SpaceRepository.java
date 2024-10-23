package kr.moonwalk.moonwalk_api.repository;

import java.util.List;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Space;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long> {

    List<Space> findByCategory(Category category);
}
