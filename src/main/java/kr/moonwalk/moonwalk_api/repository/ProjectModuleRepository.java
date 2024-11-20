package kr.moonwalk.moonwalk_api.repository;

import kr.moonwalk.moonwalk_api.domain.ProjectModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectModuleRepository extends JpaRepository<ProjectModule, Long> {

}
