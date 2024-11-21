package kr.moonwalk.moonwalk_api.repository;

import java.util.Optional;
import kr.moonwalk.moonwalk_api.domain.Module;
import kr.moonwalk.moonwalk_api.domain.Project;
import kr.moonwalk.moonwalk_api.domain.ProjectModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectModuleRepository extends JpaRepository<ProjectModule, Long>, ProjectModuleRepositoryCustom {

    Optional<ProjectModule> findByProjectAndModule(Project project, Module module);
}
