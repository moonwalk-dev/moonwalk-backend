package kr.moonwalk.moonwalk_api.config.loader;

import java.util.List;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.domain.Mood;
import kr.moonwalk.moonwalk_api.repository.MoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(5)
public class MoodLoader implements CommandLineRunner {

    private final MoodRepository moodRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (moodRepository.count() == 0) {

            Image coverImage1 = new Image("cover_image_1.jpg");
            Image coverImage2 = new Image("cover_image_2.jpg");
            Image coverImage3 = new Image("cover_image_3.jpg");

            Image detailImage1 = new Image("detail_image_1.jpg");
            Image detailImage2 = new Image("detail_image_2.jpg");
            Image detailImage3 = new Image("detail_image_3.jpg");

            Mood mood1 = new Mood("Modern", "세련되고 심플한 분위기", coverImage1);
            Mood mood2 = new Mood("Rustic", "따뜻하고 자연스러운 분위기", coverImage2);
            Mood mood3 = new Mood("Industrial", "차가운 메탈 느낌의 분위기", coverImage3);

            mood1.addDetailImages(List.of(detailImage1, detailImage2, detailImage3));

            moodRepository.saveAll(List.of(mood1, mood2, mood3));
        }
    }
}
