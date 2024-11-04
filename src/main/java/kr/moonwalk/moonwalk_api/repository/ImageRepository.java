package kr.moonwalk.moonwalk_api.repository;

import kr.moonwalk.moonwalk_api.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
