package kr.moonwalk.moonwalk_api.repository;

import java.util.List;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByCategory(Category category);

    List<Module> findByNameContainingIgnoreCase(String query);
}
