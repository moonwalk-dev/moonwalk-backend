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

            String bucketName = "moonwalk-images";
            String baseFolder = "moods/";

            Image coverImageNatural = getCoverImage(bucketName, baseFolder + "natural/");
            Image coverImageModern = getCoverImage(bucketName, baseFolder + "modern/");
            Image coverImageCasual = getCoverImage(bucketName, baseFolder + "casual/");
            Image coverImageHighEnd = getCoverImage(bucketName, baseFolder + "highend/");
            Image coverImageHighTech = getCoverImage(bucketName, baseFolder + "hightech/");
            Image coverImageCreative = getCoverImage(bucketName, baseFolder + "creative/");

            List<Image> detailImagesNatural = getDetailImages(bucketName, baseFolder + "natural/");
            List<Image> detailImagesModern = getDetailImages(bucketName, baseFolder + "modern/");
            List<Image> detailImagesCasual = getDetailImages(bucketName, baseFolder + "casual/");
            List<Image> detailImagesHighEnd = getDetailImages(bucketName, baseFolder + "highend/");
            List<Image> detailImagesHighTech = getDetailImages(bucketName, baseFolder + "hightech/");
            List<Image> detailImagesCreative = getDetailImages(bucketName, baseFolder + "creative/");

            Mood naturalMood = new Mood("내추럴", "따뜻하고 자연스러운 분위기");
            Mood modernMood = new Mood("모던", "세련되고 심플한 분위기");
            Mood casualMood = new Mood("캐주얼", "편안하고 가벼운 분위기");
            Mood highEndMood = new Mood("하이엔드", "고급스럽고 정교한 분위기");
            Mood highTechMood = new Mood("하이테크", "첨단 기술과 차가운 메탈 느낌의 분위기");
            Mood creativeMood = new Mood("크리에이티브", "독창적이고 예술적인 분위기");

            naturalMood.setCoverImage(coverImageNatural);
            modernMood.setCoverImage(coverImageModern);
            casualMood.setCoverImage(coverImageCasual);
            highEndMood.setCoverImage(coverImageHighEnd);
            highTechMood.setCoverImage(coverImageHighTech);
            creativeMood.setCoverImage(coverImageCreative);

            naturalMood.addDetailImages(detailImagesNatural);
            modernMood.addDetailImages(detailImagesModern);
            casualMood.addDetailImages(detailImagesCasual);
            highEndMood.addDetailImages(detailImagesHighEnd);
            highTechMood.addDetailImages(detailImagesHighTech);
            creativeMood.addDetailImages(detailImagesCreative);

            moodRepository.saveAll(List.of(naturalMood, modernMood, casualMood, highEndMood, highTechMood, creativeMood));
        }
    }

    private Image getCoverImage(String bucketName, String folderPath) {
        List<String> keys = getS3ImagesFromFolder(bucketName, folderPath);
        String coverImageKey = keys.stream()
            .filter(key -> key.contains("cover") && key.matches(".*\\.(png|jpg|jpeg|gif)$"))
            .findFirst()
            .orElse(null);

        return coverImageKey != null ? new Image(getS3ObjectUrl(bucketName, coverImageKey)) : null;
    }

    private List<Image> getDetailImages(String bucketName, String folderPath) {
        return getS3ImagesFromFolder(bucketName, folderPath).stream()
            .filter(imageKey -> imageKey.contains("detail") && imageKey.matches(".*\\.(png|jpg|jpeg|gif)$"))
            .map(imageKey -> new Image(getS3ObjectUrl(bucketName, imageKey)))
            .collect(Collectors.toList());
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