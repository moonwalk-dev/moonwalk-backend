package kr.moonwalk.moonwalk_api.repository;

import java.util.List;
import kr.moonwalk.moonwalk_api.domain.MyModule;
import kr.moonwalk.moonwalk_api.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyModuleRepository extends JpaRepository<MyModule, Long> {
    List<MyModule> findByProjectAndModuleNameContainingIgnoreCase(Project project, String query);
}
