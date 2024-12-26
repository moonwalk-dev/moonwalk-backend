package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.domain.Module;
import kr.moonwalk.moonwalk_api.dto.module.CategoriesModulesResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.CategoryModulesResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleSaveDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleSaveResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleSearchResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleSearchResultDto;
import kr.moonwalk.moonwalk_api.exception.notfound.CategoryNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ModuleNotFoundException;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.ModuleRepository;
import kr.moonwalk.moonwalk_api.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public CategoriesModulesResponseDto getModulesByCategoryNames(List<String> categoryNames) {
        List<Long> baseCategoryIds = categoryRepository.findIdsByNameInAndType(categoryNames, Type.TYPE_MODULE);

        List<Long> allCategoryIds = baseCategoryIds.stream()
            .flatMap(categoryId -> categoryRepository.findAllSubCategoryIdsById(categoryId, Type.TYPE_MODULE).stream())
            .distinct()
            .collect(Collectors.toList());

        List<Module> modules = moduleRepository.findByCategoryIdsAndType(allCategoryIds, Type.TYPE_MODULE);

        Map<String, List<ModuleResponseDto>> groupedModules = modules.stream()
            .collect(Collectors.groupingBy(
                module -> module.getCategory().getName(),
                Collectors.mapping(module -> {
                    String topImageUrl = module.getTopImage() != null ? module.getTopImage().getImageUrl() : null;
                    String isoImageUrl = module.getIsoImage() != null ? module.getIsoImage().getImageUrl() : null;
                    String size = module.getWidth() + "*" + module.getHeight();

                    return new ModuleResponseDto(
                        module.getId(),
                        module.getName(),
                        module.getDescription(),
                        size,
                        module.getPrice(),
                        module.getMaterials(),
                        module.getSerialNumber(),
                        module.getCapacity(),
                        topImageUrl,
                        isoImageUrl
                    );
                }, Collectors.toList())
            ));

        for (String categoryName : categoryNames) {
            groupedModules.putIfAbsent(categoryName, List.of());
        }

        List<CategoryModulesResponseDto> categoryModules = groupedModules.entrySet().stream()
            .map(entry -> new CategoryModulesResponseDto(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

        return new CategoriesModulesResponseDto(categoryModules);
    }

    @Transactional(readOnly = true)
    public ModuleResponseDto getInfo(Long moduleId) {

        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));

        String topImageUrl =
            module.getTopImage() != null ? module.getTopImage().getImageUrl() : null;
        String isoImageUrl = module.getIsoImage() != null ? module.getIsoImage().getImageUrl() : null;
        String size = module.getWidth() + "*" + module.getHeight();
        return new ModuleResponseDto(module.getId(), module.getName(), module.getDescription(),
            size, module.getPrice(), module.getMaterials(), module.getSerialNumber(),
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

    @Transactional
    public ModuleSaveResponseDto saveModule(ModuleSaveDto moduleSaveDto, MultipartFile topImageFile,
        MultipartFile isoImageFile) {

        Category category = categoryRepository.findById(moduleSaveDto.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException("존재하지 않는 카테고리입니다."));

        Module module = new Module(moduleSaveDto.getName(), moduleSaveDto.getDescription(),
            moduleSaveDto.getWidth(), moduleSaveDto.getHeight(), moduleSaveDto.getPrice(),
            moduleSaveDto.getMaterials(), moduleSaveDto.getSerialNumber(),
            moduleSaveDto.getCapacity(), category);

        if (topImageFile != null) {
            String topExtension = FileUtil.getFileExtension(topImageFile.getOriginalFilename());
            String topImagePath = "modules/" + moduleSaveDto.getName() + "/top." + topExtension;
            Image topImage = imageService.uploadAndSaveImage(topImageFile, topImagePath);
            module.setTopImage(topImage);
        }
        if (isoImageFile != null) {
            String isoExtension = FileUtil.getFileExtension(isoImageFile.getOriginalFilename());
            String isoImagePath = "modules/" + moduleSaveDto.getName() + "/iso." + isoExtension;
            Image isoImage = imageService.uploadAndSaveImage(isoImageFile, isoImagePath);
            module.setIsoImage(isoImage);
        }
        Module savedModule = moduleRepository.save(module);

        return new ModuleSaveResponseDto(savedModule.getId(), savedModule.getName(),
            savedModule.getSerialNumber(), savedModule.getCapacity(), category.getId());
    }

    @Transactional
    public void deleteModule(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));

        moduleRepository.delete(module);
    }

    public ModuleSaveResponseDto updateModule(Long moduleId, ModuleSaveDto moduleDto,
        MultipartFile topImageFile, MultipartFile isoImageFile) {

        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));

        if (moduleDto != null) {
            if (moduleDto.getName() != null) module.updateName(moduleDto.getName());
            if (moduleDto.getDescription() != null) module.updateDescription(moduleDto.getDescription());
            if (moduleDto.getWidth() != null) module.updateWidth(moduleDto.getWidth());
            if (moduleDto.getHeight() != null) module.updateHeight(moduleDto.getHeight());
            if (moduleDto.getPrice() != null) module.updatePrice(moduleDto.getPrice());
            if (moduleDto.getMaterials() != null) module.updateMaterials(moduleDto.getMaterials());
            if (moduleDto.getSerialNumber() != null) module.updateSerialNumber(moduleDto.getSerialNumber());
            if (moduleDto.getCapacity() != null) module.updateCapacity(moduleDto.getCapacity());

            if (moduleDto.getCategoryId() != null) {
                Category category = categoryRepository.findById(moduleDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
                module.updateCategory(category);
            }

            if (topImageFile != null) {
                String topExtension = FileUtil.getFileExtension(topImageFile.getOriginalFilename());
                String topImagePath = "modules/" + module.getName() + "/top." + topExtension;
                module.setTopImage(null);
                Image updatedCoverImage = imageService.updateImage(topImageFile, topImagePath, module.getTopImage());
                module.setTopImage(updatedCoverImage);
            }

            if (isoImageFile != null) {
                String isoExtension = FileUtil.getFileExtension(isoImageFile.getOriginalFilename());
                String isoImagePath = "modules/" + module.getName() + "/iso." + isoExtension;
                module.setIsoImage(null);
                Image updatedCoverImage = imageService.updateImage(isoImageFile, isoImagePath, module.getIsoImage());
                module.setIsoImage(updatedCoverImage);
            }
        }

        Module savedModule = moduleRepository.save(module);

        return new ModuleSaveResponseDto(savedModule.getId(), savedModule.getName(),
            savedModule.getSerialNumber(), savedModule.getCapacity(), module.getCategory().getId());
    }

}
