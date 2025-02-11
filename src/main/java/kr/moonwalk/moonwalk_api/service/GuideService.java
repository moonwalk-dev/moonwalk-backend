package kr.moonwalk.moonwalk_api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import kr.moonwalk.moonwalk_api.domain.Guide;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.dto.ImageDto;
import kr.moonwalk.moonwalk_api.dto.guide.CategoryGuideDto;
import kr.moonwalk.moonwalk_api.dto.guide.CategoryGuidesResponseDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideResponseDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideSaveDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideSaveResponseDto;
import kr.moonwalk.moonwalk_api.exception.notfound.CategoryNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.GuideNotFoundException;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.GuideRepository;
import kr.moonwalk.moonwalk_api.repository.ImageRepository;
import kr.moonwalk.moonwalk_api.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GuideService {

    private final GuideRepository guideRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;


    @Transactional(readOnly = true)
    public CategoryGuidesResponseDto getGuidesByCategoryName(String categoryName) {
        if (categoryName == null) {
            List<CategoryGuideDto> allGuides = guideRepository.findAll().stream()
                .map(guide -> new CategoryGuideDto(guide.getId(), guide.getName(),
                    guide.getCoverImage() != null ? new ImageDto(guide.getCoverImage().getId(), guide.getCoverImage().getImageUrl()) : null))
                .collect(Collectors.toList());
            return new CategoryGuidesResponseDto("전체", allGuides);
        }

        Category category = categoryRepository.findByNameAndType(categoryName, Type.TYPE_OFFICE)
            .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        List<CategoryGuideDto> guideDtos = guideRepository.findByCategory(category).stream()
            .map(guide -> new CategoryGuideDto(guide.getId(), guide.getName(),
                guide.getCoverImage() != null ? new ImageDto(guide.getCoverImage().getId(), guide.getCoverImage().getImageUrl()) : null))
            .collect(Collectors.toList());

        return new CategoryGuidesResponseDto(category.getName(), guideDtos);
    }

    @Transactional
    public GuideSaveResponseDto saveGuide(GuideSaveDto saveDto, MultipartFile coverImageFile,
        List<MultipartFile> detailImageFiles) {

        if (guideRepository.existsByName(saveDto.getName())) {
            throw new IllegalStateException("이미 존재하는 오피스가이드 명입니다.");
        }

        Category category = categoryRepository.findById(saveDto.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 카테고리입니다."));

        Guide guide = new Guide(saveDto.getName(), saveDto.getDescription(), saveDto.getKeywords(),
            category);

        String coverExtension = FileUtil.getFileExtension(coverImageFile.getOriginalFilename());
        String coverImagePath = "guides/" + guide.getName() + "/cover." + coverExtension;
        Image coverImage = imageService.uploadAndSaveImage(coverImageFile, coverImagePath);
        guide.setCoverImage(coverImage);

        List<Image> detailImages = new ArrayList<>();
        for (int i = 0; i < detailImageFiles.size(); i++) {
            MultipartFile file = detailImageFiles.get(i);
            String detailExtension = FileUtil.getFileExtension(file.getOriginalFilename());
            String detailImagePath =
                "guides/" + guide.getName() + "/detail" + (i + 1) + "." + detailExtension;
            Image detailImage = imageService.uploadAndSaveImage(file, detailImagePath);
            detailImages.add(detailImage);
        }
        guide.addDetailImages(detailImages);

        guideRepository.save(guide);

        return new GuideSaveResponseDto(guide.getId(), guide.getName(), guide.getDescription(),
            guide.getKeywords());
    }


    @Transactional(readOnly = true)
    public GuideResponseDto getInfo(Long guideId) {
        Guide guide = guideRepository.findById(guideId)
            .orElseThrow(() -> new GuideNotFoundException("오피스가이드를 찾을 수 없습니다."));

        ImageDto coverImageDto = null;
        if (guide.getCoverImage() != null) {
            coverImageDto = new ImageDto(
                guide.getCoverImage().getId(),
                guide.getCoverImage().getImageUrl()
            );
        }

        List<ImageDto> detailImageDtos = guide.getDetailImages().stream()
            .map(image -> new ImageDto(image.getId(), image.getImageUrl()))
            .collect(Collectors.toList());

        return new GuideResponseDto(
            guide.getId(),
            guide.getName(),
            guide.getDescription(),
            guide.getKeywords(),
            coverImageDto,
            detailImageDtos
        );
    }

    @Transactional
    public void deleteGuide(Long guideId) {
        Guide guide = guideRepository.findById(guideId)
            .orElseThrow(() -> new GuideNotFoundException("오피스가이드를 찾을 수 없습니다."));

        guideRepository.delete(guide);
    }

    public GuideSaveResponseDto updateGuide(Long guideId, GuideSaveDto guideDto, MultipartFile coverImageFile) {

        Guide guide = guideRepository.findById(guideId)
            .orElseThrow(() -> new GuideNotFoundException("오피스가이드를 찾을 수 없습니다."));

        if (guideDto != null) {
            if (guideDto.getName() != null) guide.updateName(guideDto.getName());
            if (guideDto.getDescription() != null) guide.updateDescription(guideDto.getDescription());
            if (guideDto.getKeywords() != null) guide.updateKeywords(guideDto.getKeywords());

            if (guideDto.getCategoryId() != null) {
                Category category = categoryRepository.findById(guideDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
                guide.updateCategory(category);
            }
        }

        if (coverImageFile != null) {
            String coverExtension = FileUtil.getFileExtension(coverImageFile.getOriginalFilename());
            String coverImagePath = "guides/" + guide.getName() + "/cover." + coverExtension;
            guide.setCoverImage(null);
            Image updatedCoverImage = imageService.updateImage(coverImageFile, coverImagePath, guide.getCoverImage());
            guide.setCoverImage(updatedCoverImage);
        }

        guideRepository.save(guide);

        return new GuideSaveResponseDto(guide.getId(), guide.getName(), guide.getDescription(), guide.getKeywords());
    }

    @Transactional
    public GuideResponseDto addDetailImages(Long guideId, List<MultipartFile> detailImageFiles) {
        Guide guide = guideRepository.findById(guideId)
            .orElseThrow(() -> new GuideNotFoundException("오피스가이드를 찾을 수 없습니다."));

        List<Image> newDetailImages = new ArrayList<>();
        int currentImageCount = guide.getDetailImages().size();

        for (int i = 0; i < detailImageFiles.size(); i++) {
            MultipartFile file = detailImageFiles.get(i);
            String detailExtension = FileUtil.getFileExtension(file.getOriginalFilename());
            String detailImagePath = "guides/" + guide.getName() + "/detail" + (currentImageCount + i + 1) + "." + detailExtension;
            Image newDetailImage = imageService.uploadAndSaveImage(file, detailImagePath);
            newDetailImages.add(newDetailImage);
        }
        guide.addDetailImages(newDetailImages);

        return getInfo(guideId);
    }

    @Transactional
    public void deleteDetailImage(Long guideId, Long imageId) {
        Guide guide = guideRepository.findById(guideId)
            .orElseThrow(() -> new GuideNotFoundException("오피스가이드를 찾을 수 없습니다."));

        Image image = imageRepository.findById(imageId)
            .orElseThrow(() -> new IllegalArgumentException("해당 이미지를 찾을 수 없습니다."));

        if (!guide.getDetailImages().contains(image)) {
            throw new IllegalArgumentException("해당 이미지가 이 가이드에 속하지 않습니다.");
        }

        guide.getDetailImages().remove(image);
        imageService.deleteImage(image);
    }

}
