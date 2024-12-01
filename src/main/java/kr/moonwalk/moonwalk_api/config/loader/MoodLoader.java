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

            String baseUrl = "https://moonwalk-project.s3.ap-northeast-2.amazonaws.com/moods/";

            Image coverImageNatural = new Image(baseUrl + "natural/cover.jpg");
            Image coverImageModern = new Image(baseUrl + "modern/cover.jpg");
            Image coverImageCasual = new Image(baseUrl + "casual/cover.jpg");
            Image coverImageHighEnd = new Image(baseUrl + "highend/cover.jpg");
            Image coverImageHighTech = new Image(baseUrl + "hightech/cover.jpg");

            List<Image> detailImagesNatural = List.of(
                new Image(baseUrl + "natural/detail1.jpg"),
                new Image(baseUrl + "natural/detail2.jpg"),
                new Image(baseUrl + "natural/detail3.jpg"),
                new Image(baseUrl + "natural/detail4.jpg"),
                new Image(baseUrl + "natural/detail5.jpg"),
                new Image(baseUrl + "natural/detail6.jpg"),
                new Image(baseUrl + "natural/detail7.jpg"),
                new Image(baseUrl + "natural/detail8.jpg"),
                new Image(baseUrl + "natural/detail9.jpg"),
                new Image(baseUrl + "natural/detail10.jpg"),
                new Image(baseUrl + "natural/detail11.jpg"),
                new Image(baseUrl + "natural/detail12.jpg"),
                new Image(baseUrl + "natural/detail13.jpg"),
                new Image(baseUrl + "natural/detail14.jpg"),
                new Image(baseUrl + "natural/detail15.jpg"),
                new Image(baseUrl + "natural/detail16.jpg")
            );

            Mood naturalMood = new Mood("내추럴", "따뜻하고 자연스러운 분위기", coverImageNatural);
            Mood modernMood = new Mood("모던", "세련되고 심플한 분위기", coverImageModern);
            Mood casualMood = new Mood("캐주얼", "편안하고 가벼운 분위기", coverImageCasual);
            Mood highEndMood = new Mood("하이엔드", "고급스럽고 정교한 분위기", coverImageHighEnd);
            Mood highTechMood = new Mood("하이테크", "첨단 기술과 차가운 메탈 느낌의 분위기", coverImageHighTech);

            naturalMood.addDetailImages(detailImagesNatural);

            moodRepository.saveAll(List.of(naturalMood, modernMood, casualMood, highEndMood, highTechMood));
        }
    }
}
