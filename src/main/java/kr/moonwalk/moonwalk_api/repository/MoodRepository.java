package kr.moonwalk.moonwalk_api.repository;

import kr.moonwalk.moonwalk_api.domain.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodRepository extends JpaRepository<Mood, Long> {

}
