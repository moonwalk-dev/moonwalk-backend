package kr.moonwalk.moonwalk_api.repository;

import java.util.List;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {

    List<Guide> findByCategory(Category category);

    boolean existsByName(String name);
}
