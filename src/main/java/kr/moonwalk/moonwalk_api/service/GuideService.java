package kr.moonwalk.moonwalk_api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Guide;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.dto.guide.CategoryGuideDto;
import kr.moonwalk.moonwalk_api.dto.guide.CategoryGuidesResponseDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideResponseDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideSaveDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideSaveResponseDto;
import kr.moonwalk.moonwalk_api.exception.notfound.CategoryNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.MoodNotFoundException;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.GuideRepository;
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


    @Transactional(readOnly = true)
    public CategoryGuidesResponseDto getGuidesByCategoryId(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        List<CategoryGuideDto> guideDtos = null;
        if (category != null) {
            guideDtos = guideRepository.findByCategory(category).stream().map(
                guide -> new CategoryGuideDto(guide.getId(), guide.getName(),
                    guide.getCoverImage() != null ? guide.getCoverImage().getImageUrl()
                        : null)).collect(Collectors.toList());

        }

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

        String coverExtension = getFileExtension(coverImageFile.getOriginalFilename());
        String coverImagePath = "guides/" + guide.getName() + "/cover." + coverExtension;
        Image coverImage = imageService.uploadAndSaveImage(coverImageFile, coverImagePath);
        guide.setCoverImage(coverImage);

        List<Image> detailImages = new ArrayList<>();
        for (int i = 0; i < detailImageFiles.size(); i++) {
            MultipartFile file = detailImageFiles.get(i);
            String detailExtension = getFileExtension(file.getOriginalFilename());
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

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }

    @Transactional(readOnly = true)
    public GuideResponseDto getInfo(Long guideId) {
        Guide guide = guideRepository.findById(guideId)
            .orElseThrow(() -> new MoodNotFoundException("오피스가이드를 찾을 수 없습니다."));

        String coverImageUrl = (guide.getCoverImage() != null) ? guide.getCoverImage().getImageUrl() : null;

        List<String> detailImageUrls = guide.getDetailImages().stream()
            .map(Image::getImageUrl)
            .collect(Collectors.toList());

        return new GuideResponseDto(
            guide.getId(),
            guide.getName(),
            guide.getDescription(),
            guide.getKeywords(),
            coverImageUrl,
            detailImageUrls
        );
    }
}
