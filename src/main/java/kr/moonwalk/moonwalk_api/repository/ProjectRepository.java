package kr.moonwalk.moonwalk_api.repository;

import kr.moonwalk.moonwalk_api.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

}
