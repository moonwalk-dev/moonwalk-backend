package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.domain.Guide;
import kr.moonwalk.moonwalk_api.dto.category.CategoryGuideDto;
import kr.moonwalk.moonwalk_api.dto.category.CategoryGuidesResponseDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideSaveDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideSaveResponseDto;
import kr.moonwalk.moonwalk_api.exception.category.CategoryNotFoundException;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.GuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GuideService {

    private final GuideRepository guideRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;


    public CategoryGuidesResponseDto getGuidesByCategoryId(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        List<CategoryGuideDto> guideDtos = null;
        if (category != null) {
            guideDtos = guideRepository.findByCategory(category).stream().map(
                guide -> new CategoryGuideDto(guide.getId(), guide.getName(),
                    guide.getCoverImage() != null ? guide.getCoverImage().getImageUrl()
                        : "default-image-url")).collect(Collectors.toList());

        }

        return new CategoryGuidesResponseDto(category.getName(), guideDtos);
    }

    public GuideSaveResponseDto saveGuide(GuideSaveDto saveDto, MultipartFile coverImageFile,
        List<MultipartFile> detailImageFiles) {

        if (guideRepository.existsByName(saveDto.getName())) {
            throw new IllegalStateException("이미 존재하는 가이드 명입니다.");
        }

        Category category = categoryRepository.findById(saveDto.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 카테고리입니다."));

        Guide guide = new Guide(saveDto.getName(), saveDto.getDescription(), saveDto.getKeywords(),
            category);

        Image coverImage = imageService.uploadAndSaveImage(coverImageFile,
            "guides/" + guide.getName() + "/cover");
        guide.setCoverImage(coverImage);

        List<Image> detailImages = detailImageFiles.stream()
            .map(file -> imageService.uploadAndSaveImage(file,
                "guides/" + guide.getName() + "/details/" + file.getOriginalFilename()))
            .collect(Collectors.toList());
        guide.addDetailImages(detailImages);

        guideRepository.save(guide);

        return new GuideSaveResponseDto(
            guide.getId(),
            guide.getName(),
            guide.getDescription(),
            guide.getKeywords()
        );
    }

}
