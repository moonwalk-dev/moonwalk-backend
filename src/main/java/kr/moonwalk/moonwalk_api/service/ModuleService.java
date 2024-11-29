package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import kr.moonwalk.moonwalk_api.domain.Module;
import kr.moonwalk.moonwalk_api.dto.module.CategoriesModulesResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.CategoryModuleDto;
import kr.moonwalk.moonwalk_api.dto.module.CategoryModulesResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleSearchResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleSearchResultDto;
import kr.moonwalk.moonwalk_api.exception.notfound.CategoryNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ModuleNotFoundException;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public CategoriesModulesResponseDto getModulesByCategoryNames(List<String> categoryNames) {
        List<Module> modules = moduleRepository.findByCategoryNamesAndType(categoryNames, Type.TYPE_MODULE);

        if (modules.isEmpty()) {
            throw new CategoryNotFoundException("해당 카테고리에 모듈이 존재하지 않습니다.");
        }

        Map<String, List<CategoryModuleDto>> groupedModules = modules.stream()
            .collect(Collectors.groupingBy(
                module -> module.getCategory().getName(),
                Collectors.mapping(module -> new CategoryModuleDto(
                    module.getId(),
                    module.getName(),
                    module.getCapacity(),
                    module.getSerialNumber(),
                    module.getIsoImage() != null ? module.getIsoImage().getImageUrl() : null
                ), Collectors.toList())
            ));

        List<CategoryModulesResponseDto> categoryModules = groupedModules.entrySet().stream()
            .map(entry -> new CategoryModulesResponseDto(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

        return new CategoriesModulesResponseDto(categoryModules);
    }


    @Transactional(readOnly = true)
    public ModuleResponseDto getInfo(Long moduleId) {

        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));

        String topImageUrl = module.getTopImage().getImageUrl();
        String isoImageUrl = module.getIsoImage().getImageUrl();
        return new ModuleResponseDto(module.getId(), module.getName(), module.getDescription(),
            module.getSize(), module.getPrice(), module.getMaterial(), module.getSerialNumber(),
            module.getCapacity(), topImageUrl, isoImageUrl);
    }

    @Transactional(readOnly = true)
    public ModuleSearchResultDto searchModulesByName(String query) {
        List<Module> modules = moduleRepository.findByNameContainingIgnoreCase(query);

        List<ModuleSearchResponseDto> moduleDtos = modules.stream()
            .map(module -> new ModuleSearchResponseDto(
                module.getId(),
                module.getName(),
                module.getCapacity(),
                module.getSerialNumber(),
                module.getIsoImage() != null ? module.getIsoImage().getImageUrl() : null
            ))
            .collect(Collectors.toList());

        return new ModuleSearchResultDto(query, moduleDtos);
    }
}
