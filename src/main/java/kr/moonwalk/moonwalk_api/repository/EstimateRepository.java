package kr.moonwalk.moonwalk_api.repository;

import kr.moonwalk.moonwalk_api.domain.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {

}
