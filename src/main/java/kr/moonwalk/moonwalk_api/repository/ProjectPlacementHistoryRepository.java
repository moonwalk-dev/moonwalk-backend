package kr.moonwalk.moonwalk_api.repository;

import java.util.Optional;
import kr.moonwalk.moonwalk_api.domain.ProjectPlacementHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectPlacementHistoryRepository extends JpaRepository<ProjectPlacementHistory, Long> {

    Optional<ProjectPlacementHistory> findTopByProjectIdOrderByTimestampDesc(Long projectId);
}
