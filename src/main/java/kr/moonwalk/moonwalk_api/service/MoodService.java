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

        String coverImageUrl = (mood.getCoverImage() != null) ? mood.getCoverImage().getImageUrl() : null;

        List<String> detailImageUrls = mood.getDetailImages().stream()
            .map(Image::getImageUrl)
            .collect(Collectors.toList());

        return new MoodResponseDto(
            mood.getId(),
            mood.getName(),
            mood.getDescription(),
            coverImageUrl,
            detailImageUrls
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
    public MoodSaveResponseDto updateMood(Long moodId, MoodSaveDto moodDto, MultipartFile coverImageFile,
        List<MultipartFile> detailImageFiles) {

        Mood mood = moodRepository.findById(moodId)
            .orElseThrow(() -> new MoodNotFoundException("오피스가이드를 찾을 수 없습니다."));

        if (moodDto != null) {
            if (moodDto.getName() != null) mood.updateName(moodDto.getName());
            if (moodDto.getDescription() != null) mood.updateDescription(moodDto.getDescription());
        }

        if (coverImageFile != null) {
            String coverExtension = FileUtil.getFileExtension(coverImageFile.getOriginalFilename());
            String coverImagePath = "moods/" + mood.getName() + "/cover." + coverExtension;
            mood.setCoverImage(null);
            Image updatedCoverImage = imageService.updateImage(coverImageFile, coverImagePath, mood.getCoverImage());
            mood.setCoverImage(updatedCoverImage);
        }

        if (detailImageFiles != null && !detailImageFiles.isEmpty()) {
            List<Image> existingDetailImages = mood.getDetailImages();
            mood.getDetailImages().clear();
            for (Image existingImage : existingDetailImages) {
                imageService.deleteImage(existingImage);
            }

            List<Image> newDetailImages = new ArrayList<>();
            for (int i = 0; i < detailImageFiles.size(); i++) {
                MultipartFile file = detailImageFiles.get(i);
                String detailExtension = FileUtil.getFileExtension(file.getOriginalFilename());
                String detailImagePath = "moods/" + mood.getName() + "/detail" + (i + 1) + "." + detailExtension;
                Image newDetailImage = imageService.uploadAndSaveImage(file, detailImagePath);
                newDetailImages.add(newDetailImage);
            }
            mood.addDetailImages(newDetailImages);
        }

        moodRepository.save(mood);

        return new MoodSaveResponseDto(mood.getId(), mood.getName(), mood.getDescription());
    }
}
