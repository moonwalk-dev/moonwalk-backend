package kr.moonwalk.moonwalk_api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.domain.Mood;
import kr.moonwalk.moonwalk_api.dto.mood.MoodDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodListResponseDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodResponseDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodSaveDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodSaveResponseDto;
import kr.moonwalk.moonwalk_api.exception.notfound.MoodNotFoundException;
import kr.moonwalk.moonwalk_api.repository.ImageRepository;
import kr.moonwalk.moonwalk_api.repository.MoodRepository;
import kr.moonwalk.moonwalk_api.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final MoodRepository moodRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    @Transactional(readOnly = true)
    public MoodListResponseDto getAllMoods() {
        List<MoodDto> moods = moodRepository.findAll().stream()
            .map(mood -> new MoodDto(
                mood.getId(),
                mood.getName(),
                mood.getCoverImage() != null ? mood.getCoverImage().getImageUrl() : null
            ))
            .collect(Collectors.toList());

        return new MoodListResponseDto(moods);
    }

    @Transactional(readOnly = true)
    public MoodResponseDto getInfo(Long moodId) {
        Mood mood = moodRepository.findById(moodId)
            .orElseThrow(() -> new MoodNotFoundException("무드를 찾을 수 없습니다."));

        MoodResponseDto.ImageDto coverImageDto = null;
        if (mood.getCoverImage() != null) {
            coverImageDto = new MoodResponseDto.ImageDto(
                mood.getCoverImage().getId(),
                mood.getCoverImage().getImageUrl()
            );
        }

        List<MoodResponseDto.ImageDto> detailImageDtos = mood.getDetailImages().stream()
            .map(image -> new MoodResponseDto.ImageDto(image.getId(), image.getImageUrl()))
            .collect(Collectors.toList());

        return new MoodResponseDto(
            mood.getId(),
            mood.getName(),
            mood.getDescription(),
            coverImageDto,
            detailImageDtos
        );
    }

    @Transactional
    public MoodSaveResponseDto saveMood(MoodSaveDto moodDto, MultipartFile coverImageFile,
        List<MultipartFile> detailImageFiles) {
        if (moodRepository.existsByName(moodDto.getName())) {
            throw new IllegalStateException("이미 존재하는 무드 명입니다.");
        }

        Mood mood = new Mood(moodDto.getName(), moodDto.getDescription());

        String coverExtension = FileUtil.getFileExtension(coverImageFile.getOriginalFilename());
        String coverImagePath = "moods/" + mood.getName() + "/cover." + coverExtension;
        Image coverImage = imageService.uploadAndSaveImage(coverImageFile, coverImagePath);
        mood.setCoverImage(coverImage);

        List<Image> detailImages = new ArrayList<>();
        for (int i = 0; i < detailImageFiles.size(); i++) {
            MultipartFile file = detailImageFiles.get(i);
            String detailExtension = FileUtil.getFileExtension(file.getOriginalFilename());
            String detailImagePath =
                "moods/" + mood.getName() + "/detail" + (i + 1) + "." + detailExtension;
            Image detailImage = imageService.uploadAndSaveImage(file, detailImagePath);
            detailImages.add(detailImage);
        }
        mood.addDetailImages(detailImages);

        moodRepository.save(mood);

        return new MoodSaveResponseDto(mood.getId(), mood.getName(), mood.getDescription());
    }

    @Transactional
    public void deleteMood(Long moodId) {
        Mood mood = moodRepository.findById(moodId)
            .orElseThrow(() -> new MoodNotFoundException("무드를 찾을 수 없습니다."));

        moodRepository.delete(mood);
    }

    @Transactional
    public void deleteDetailImage(Long moodId, Long imageId) {
        Mood mood = moodRepository.findById(moodId)
            .orElseThrow(() -> new MoodNotFoundException("무드를 찾을 수 없습니다."));

        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new IllegalArgumentException("해당 이미지를 찾을 수 없습니다."));

        if (!mood.getDetailImages().contains(image)) {
            throw new IllegalArgumentException("해당 이미지가 이 무드에 속하지 않습니다.");
        }

        mood.getDetailImages().remove(image);
        imageService.deleteImage(image);
    }

    @Transactional
    public MoodResponseDto addDetailImages(Long moodId, List<MultipartFile> detailImageFiles) {
        Mood mood = moodRepository.findById(moodId)
            .orElseThrow(() -> new MoodNotFoundException("무드를 찾을 수 없습니다."));

        List<Image> newImages = new ArrayList<>();
        int currentImageCount = mood.getDetailImages().size();

        for (int i = 0; i < detailImageFiles.size(); i++) {
            MultipartFile file = detailImageFiles.get(i);
            String extension = FileUtil.getFileExtension(file.getOriginalFilename());
            String imagePath = String.format("moods/%s/detail%d.%s",
                mood.getName(),
                currentImageCount + i + 1,
                extension);
            Image image = imageService.uploadAndSaveImage(file, imagePath);
            newImages.add(image);
        }

        mood.addDetailImages(newImages);

        return getInfo(moodId);
    }

    @Transactional
    public MoodSaveResponseDto updateMood(Long moodId, MoodSaveDto moodDto, MultipartFile coverImageFile) {
        Mood mood = moodRepository.findById(moodId)
            .orElseThrow(() -> new MoodNotFoundException("무드를 찾을 수 없습니다."));

        if (moodDto != null) {
            if (moodDto.getName() != null) mood.updateName(moodDto.getName());
            if (moodDto.getDescription() != null) mood.updateDescription(moodDto.getDescription());
        }

        if (coverImageFile != null) {
            String coverExtension = FileUtil.getFileExtension(coverImageFile.getOriginalFilename());
            String coverImagePath = "moods/" + mood.getName() + "/cover." + coverExtension;
            Image updatedCoverImage = imageService.updateImage(coverImageFile, coverImagePath, mood.getCoverImage());
            mood.setCoverImage(updatedCoverImage);
        }

        moodRepository.save(mood);

        return new MoodSaveResponseDto(mood.getId(), mood.getName(), mood.getDescription());
    }
}
