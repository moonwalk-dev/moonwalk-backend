package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.dto.category.CategorySpaceDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySpacesResponseDto;
import kr.moonwalk.moonwalk_api.exception.category.CategoryNotFoundException;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final CategoryRepository categoryRepository;


    public CategorySpacesResponseDto getSpacesByCategoryId(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        List<CategorySpaceDto> spaceDtos = null;
        if (category != null) {
            spaceDtos = spaceRepository.findByCategory(category).stream().map(
                space -> new CategorySpaceDto(space.getId(), space.getName(),
                    space.getCoverImage() != null ? space.getCoverImage().getImageUrl()
                        : "default-image-url")).collect(Collectors.toList());

        }

        return new CategorySpacesResponseDto(category.getName(), spaceDtos);
    }
}
