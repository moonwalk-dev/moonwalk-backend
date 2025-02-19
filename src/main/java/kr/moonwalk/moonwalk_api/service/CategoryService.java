package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import kr.moonwalk.moonwalk_api.dto.category.CategoryListResponseDto;
import kr.moonwalk.moonwalk_api.dto.category.CategoryResponseDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySaveDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySaveResponseDto;
import kr.moonwalk.moonwalk_api.dto.category.CategoryUpdateDto;
import kr.moonwalk.moonwalk_api.exception.notfound.CategoryNotFoundException;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategorySaveResponseDto create(CategorySaveDto saveDto, Type type) {

        Category parentCategory = null;

        if (categoryRepository.existsByNameAndType(saveDto.getName(), type)) {
            throw new IllegalStateException("이미 존재하는 카테고리 명입니다.");
        }

        if (saveDto.getParentId() != null) {
            parentCategory = categoryRepository.findById(saveDto.getParentId())
                .orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 부모 카테고리입니다."));

            if (parentCategory.getType() != type) {
                throw new CategoryNotFoundException("존재하지 않는 부모 카테고리입니다.");
            }
        }

        Category category = new Category(saveDto.getName(), parentCategory, type);
        Category savedCategory = categoryRepository.save(category);

        return new CategorySaveResponseDto(savedCategory.getId(), savedCategory.getName());
    }

    @Transactional(readOnly = true)
    public CategoryListResponseDto getOfficeGuideCategories() {
        List<CategoryResponseDto> categories = categoryRepository.findAll().stream()
            .filter(category -> (category.getParentCategory() == null && category.getType() == Type.TYPE_OFFICE))
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return new CategoryListResponseDto(categories);
    }

    @Transactional(readOnly = true)
    public CategoryListResponseDto getModuleCategories() {
        List<CategoryResponseDto> categories = categoryRepository.findAll().stream()
            .filter(category -> (category.getParentCategory() == null && category.getType() == Type.TYPE_MODULE))
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

    @Transactional
    public CategorySaveResponseDto updateOfficeGuideCategory(Long categoryId,
        CategoryUpdateDto updateDto) {

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        if (category.getType() != Type.TYPE_OFFICE) {
            throw new IllegalStateException("오피스가이드 카테고리가 아닙니다.");
        }

        if (categoryRepository.existsByNameAndType(updateDto.getName(), category.getType())) {
            throw new IllegalStateException("이미 존재하는 카테고리 명입니다.");
        }

        category.updateName(updateDto.getName());

        return new CategorySaveResponseDto(category.getId(), category.getName());
    }

    @Transactional
    public CategorySaveResponseDto updateModuleCategory(Long categoryId,
        CategoryUpdateDto updateDto) {

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        if (category.getType() != Type.TYPE_MODULE) {
            throw new IllegalStateException("모듈 카테고리가 아닙니다.");
        }

        if (categoryRepository.existsByNameAndType(updateDto.getName(), category.getType())) {
            throw new IllegalStateException("이미 존재하는 카테고리 명입니다.");
        }

        category.updateName(updateDto.getName());

        return new CategorySaveResponseDto(category.getId(), category.getName());
    }

    @Transactional
    public void deleteOfficeGuideCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        if (category.getType() != Type.TYPE_OFFICE) {
            throw new IllegalStateException("오피스가이드 카테고리가 아닙니다.");
        }

        categoryRepository.delete(category);
    }

    @Transactional
    public void deleteModuleCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        if (category.getType() != Type.TYPE_MODULE) {
            throw new IllegalStateException("모듈 카테고리가 아닙니다.");
        }

        categoryRepository.delete(category);
    }
}
