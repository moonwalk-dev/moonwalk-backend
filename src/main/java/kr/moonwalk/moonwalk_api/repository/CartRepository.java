package kr.moonwalk.moonwalk_api.repository;

import java.util.Optional;
import kr.moonwalk.moonwalk_api.domain.Cart;
import kr.moonwalk.moonwalk_api.domain.Estimate;
import kr.moonwalk.moonwalk_api.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByEstimateAndModule(Estimate estimate, Module module);

}
