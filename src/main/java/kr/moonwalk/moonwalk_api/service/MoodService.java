package kr.moonwalk.moonwalk_api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.domain.Mood;
import kr.moonwalk.moonwalk_api.dto.ImageDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodListResponseDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodResponseDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodSaveDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodSaveResponseDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodUpdateDto;
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

        ImageDto coverImageDto = null;
        if (mood.getCoverImage() != null) {
            coverImageDto = new ImageDto(
                mood.getCoverImage().getId(),
                mood.getCoverImage().getImageUrl()
            );
        }

        List<ImageDto> detailImageDtos = mood.getDetailImages().stream()
            .map(image -> new ImageDto(image.getId(), image.getImageUrl()))
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
        for (MultipartFile file : detailImageFiles) {
            String fileName = UUID.randomUUID().toString();
            String detailExtension = FileUtil.getFileExtension(file.getOriginalFilename());
            String detailImagePath = "moods/" + mood.getName() + "/detail_" + fileName + "." + detailExtension;
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
    public MoodResponseDto updateMood(Long moodId, MoodUpdateDto updateDto,
        MultipartFile coverImageFile, List<MultipartFile> detailImageFiles) {

        Mood mood = moodRepository.findById(moodId)
            .orElseThrow(() -> new MoodNotFoundException("무드를 찾을 수 없습니다."));

        if (updateDto != null && updateDto.getMood() != null) {
            MoodSaveDto moodDto = updateDto.getMood();
            if (moodDto.getName() != null) mood.updateName(moodDto.getName());
            if (moodDto.getDescription() != null) mood.updateDescription(moodDto.getDescription());
        }

        if (coverImageFile != null) {
            String coverExtension = FileUtil.getFileExtension(coverImageFile.getOriginalFilename());
            String coverImagePath = "moods/" + mood.getName() + "/cover." + coverExtension;
            Image updatedCoverImage = imageService.updateImage(coverImageFile, coverImagePath, mood.getCoverImage());
            mood.setCoverImage(updatedCoverImage);
        }

        if (updateDto != null && updateDto.getDeleteImageIds() != null) {
            for (Long imageId : updateDto.getDeleteImageIds()) {
                deleteDetailImage(moodId, imageId);
            }
        }

        if (detailImageFiles != null && !detailImageFiles.isEmpty()) {
            addDetailImages(moodId, detailImageFiles);
        }

        moodRepository.save(mood);

        return getInfo(moodId);
    }

    @Transactional
    public MoodResponseDto addDetailImages(Long moodId, List<MultipartFile> detailImageFiles) {
        Mood mood = moodRepository.findById(moodId)
            .orElseThrow(() -> new MoodNotFoundException("무드를 찾을 수 없습니다."));

        List<Image> newDetailImages = new ArrayList<>();
        for (MultipartFile file : detailImageFiles) {
            String fileName = UUID.randomUUID().toString();
            String detailExtension = FileUtil.getFileExtension(file.getOriginalFilename());
            String detailImagePath = "moods/" + mood.getName() + "/detail_" + fileName + "." + detailExtension;
            Image newDetailImage = imageService.uploadAndSaveImage(file, detailImagePath);
            newDetailImages.add(newDetailImage);
        }
        mood.addDetailImages(newDetailImages);

        return getInfo(moodId);
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
}
