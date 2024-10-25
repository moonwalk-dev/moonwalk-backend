package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.dto.category.CategoryListResponseDto;
import kr.moonwalk.moonwalk_api.dto.category.CategoryResponseDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySaveDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySaveResponseDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySpaceDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySpacesResponseDto;
import kr.moonwalk.moonwalk_api.exception.category.CategoryNotFoundException;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final SpaceRepository spaceRepository;
    private final CategoryRepository categoryRepository;

    public CategorySpacesResponseDto getSpacesById(Long categoryId) {

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

    public CategorySaveResponseDto create(CategorySaveDto categorySaveDto) {

        Category parentCategory =
            (categorySaveDto.getParentId() != null) ? categoryRepository.findById(
                categorySaveDto.getParentId()).orElse(null) : null;

        Category category = new Category(categorySaveDto.getName(), parentCategory);
        Category savedCategory = categoryRepository.save(category);

        return new CategorySaveResponseDto(savedCategory.getId(), savedCategory.getName());
    }

    public CategoryListResponseDto getAllCategories() {
        List<CategoryResponseDto> categories = categoryRepository.findAll().stream()
            .filter(category -> category.getParentCategory() == null)
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return new CategoryListResponseDto(categories);
    }

    private CategoryResponseDto convertToDto(Category category) {
        List<CategoryResponseDto> subCategories = category.getSubCategories().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return new CategoryResponseDto(category.getId(), category.getName(), subCategories);
    }
}
