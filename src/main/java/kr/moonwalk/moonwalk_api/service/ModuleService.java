package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.dto.module.CategoryModuleDto;
import kr.moonwalk.moonwalk_api.dto.module.CategoryModulesResponseDto;
import kr.moonwalk.moonwalk_api.exception.category.CategoryNotFoundException;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CategoryRepository categoryRepository;

    public CategoryModulesResponseDto getModulesByCategoryId(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));

        List<CategoryModuleDto> moduleDtos = null;
        if (category != null) {
            moduleDtos = moduleRepository.findByCategory(category).stream().map(
                module -> new CategoryModuleDto(module.getId(), module.getName(),
                    module.getCapacity(), module.getSerialNumber(),
                    module.getIsoImage() != null ? module.getIsoImage().getImageUrl()
                        : "default-image-url")).collect(Collectors.toList());

        }

        return new CategoryModulesResponseDto(category.getName(), moduleDtos);
    }

}
