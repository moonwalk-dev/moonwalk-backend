package kr.moonwalk.moonwalk_api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    public Image uploadAndSaveImage(MultipartFile file, String filePath) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucketName, filePath, file.getInputStream(), metadata));
            String imageUrl = amazonS3.getUrl(bucketName, filePath).toString();

            Image image = new Image(imageUrl);
            return imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to S3", e);
        }
    }

    public void deleteImage(Image image) {
        try {
            String filePath = extractFilePathFromUrl(image.getImageUrl());
            amazonS3.deleteObject(bucketName, filePath);

            imageRepository.delete(image);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from S3", e);
        }
    }

    public Image updateImage(MultipartFile file, String filePath, Image existingImage) {
        if (existingImage != null) {
            deleteImage(existingImage);
        }

        return uploadAndSaveImage(file, filePath);
    }

    private String extractFilePathFromUrl(String imageUrl) {
        String baseUrl = "https://" + bucketName + ".s3.amazonaws.com/";
        return imageUrl.replace(baseUrl, "");
    }

}