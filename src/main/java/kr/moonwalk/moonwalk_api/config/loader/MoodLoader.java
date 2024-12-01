package kr.moonwalk.moonwalk_api.config.loader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.List;
import java.util.stream.Collectors;
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
    private final AmazonS3 amazonS3;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (moodRepository.count() == 0) {

            String bucketName = "moonwalk-project";
            String baseFolder = "moods/";

            Image coverImageNatural = new Image(getS3ObjectUrl(bucketName, baseFolder + "natural/cover.png"));
            Image coverImageModern = new Image(getS3ObjectUrl(bucketName, baseFolder + "modern/cover.png"));
            Image coverImageCasual = new Image(getS3ObjectUrl(bucketName, baseFolder + "casual/cover.png"));
            Image coverImageHighEnd = new Image(getS3ObjectUrl(bucketName, baseFolder + "highend/cover.png"));
            Image coverImageHighTech = new Image(getS3ObjectUrl(bucketName, baseFolder + "hightech/cover.png"));
            Image coverImageCreative = new Image(getS3ObjectUrl(bucketName, baseFolder + "creative/cover.png"));

            List<Image> detailImagesNatural = getS3ImagesFromFolder(bucketName, baseFolder + "natural/").stream()
                .filter(imageKey -> imageKey.contains("detail"))
                .map(imageKey -> new Image(getS3ObjectUrl(bucketName, imageKey)))
                .collect(Collectors.toList());

            List<Image> detailImagesModern = getS3ImagesFromFolder(bucketName, baseFolder + "modern/").stream()
                .filter(imageKey -> imageKey.contains("detail"))
                .map(imageKey -> new Image(getS3ObjectUrl(bucketName, imageKey)))
                .collect(Collectors.toList());

            List<Image> detailImagesCasual = getS3ImagesFromFolder(bucketName, baseFolder + "casual/").stream()
                .filter(imageKey -> imageKey.contains("detail"))
                .map(imageKey -> new Image(getS3ObjectUrl(bucketName, imageKey)))
                .collect(Collectors.toList());

            List<Image> detailImagesHighEnd = getS3ImagesFromFolder(bucketName, baseFolder + "highend/").stream()
                .filter(imageKey -> imageKey.contains("detail"))
                .map(imageKey -> new Image(getS3ObjectUrl(bucketName, imageKey)))
                .collect(Collectors.toList());

            List<Image> detailImagesHighTech = getS3ImagesFromFolder(bucketName, baseFolder + "hightech/").stream()
                .filter(imageKey -> imageKey.contains("detail"))
                .map(imageKey -> new Image(getS3ObjectUrl(bucketName, imageKey)))
                .collect(Collectors.toList());

            List<Image> detailImagesCreative = getS3ImagesFromFolder(bucketName, baseFolder + "creative/").stream()
                .filter(imageKey -> imageKey.contains("detail"))
                .map(imageKey -> new Image(getS3ObjectUrl(bucketName, imageKey)))
                .collect(Collectors.toList());

            Mood naturalMood = new Mood("내추럴", "따뜻하고 자연스러운 분위기", coverImageNatural);
            Mood modernMood = new Mood("모던", "세련되고 심플한 분위기", coverImageModern);
            Mood casualMood = new Mood("캐주얼", "편안하고 가벼운 분위기", coverImageCasual);
            Mood highEndMood = new Mood("하이엔드", "고급스럽고 정교한 분위기", coverImageHighEnd);
            Mood highTechMood = new Mood("하이테크", "첨단 기술과 차가운 메탈 느낌의 분위기", coverImageHighTech);
            Mood creativeMood = new Mood("크리에이티브", "독창적이고 예술적인 분위기", coverImageCreative);

            naturalMood.addDetailImages(detailImagesNatural);
            modernMood.addDetailImages(detailImagesModern);
            casualMood.addDetailImages(detailImagesCasual);
            highEndMood.addDetailImages(detailImagesHighEnd);
            highTechMood.addDetailImages(detailImagesHighTech);
            creativeMood.addDetailImages(detailImagesCreative);

            moodRepository.saveAll(List.of(naturalMood, modernMood, casualMood, highEndMood, highTechMood, creativeMood));
        }
    }

    private List<String> getS3ImagesFromFolder(String bucketName, String folderPath) {
        ListObjectsV2Request request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(folderPath);

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        return result.getObjectSummaries().stream()
            .map(S3ObjectSummary::getKey)
            .collect(Collectors.toList());
    }

    private String getS3ObjectUrl(String bucketName, String key) {
        return amazonS3.getUrl(bucketName, key).toString();
    }
}
