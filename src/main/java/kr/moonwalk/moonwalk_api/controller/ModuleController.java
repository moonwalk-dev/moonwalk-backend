package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import kr.moonwalk.moonwalk_api.dto.module.CategoriesModulesResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleSaveDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleSaveResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleSearchResultDto;
import kr.moonwalk.moonwalk_api.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @Operation(summary = "카테고리별 모듈 조회")
    @GetMapping
    public ResponseEntity<CategoriesModulesResponseDto> getModulesByCategories(
        @RequestParam List<String> categoryNames) {

        CategoriesModulesResponseDto response = moduleService.getModulesByCategoryNames(categoryNames);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 모듈 조회")
    @GetMapping("/{moduleId}")
    public ResponseEntity<ModuleResponseDto> getModuleInfo(@PathVariable Long moduleId) {

        ModuleResponseDto response = moduleService.getInfo(moduleId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모듈 이름으로 검색")
    @GetMapping("/search")
    public ResponseEntity<ModuleSearchResultDto> searchModulesByName(
        @RequestParam("query") String query) {
        ModuleSearchResultDto response = moduleService.searchModulesByName(query);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 전용 모듈 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuleSaveResponseDto> createModule(
        @RequestPart("module") ModuleSaveDto moduleSaveDto,
        @RequestPart("topImage") @Parameter(description = "Top image file") MultipartFile topImageFile,
        @RequestPart("isoImage") @Parameter(description = "Iso image file") MultipartFile isoImageFile) {

        ModuleSaveResponseDto response = moduleService.saveModule(moduleSaveDto, topImageFile, isoImageFile);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 전용 모듈 삭제")
    @DeleteMapping("/{moduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteModule(@PathVariable Long moduleId) {

        moduleService.deleteModule(moduleId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "관리자 전용 모듈 수정", description = "변경하고자 하는 필드만 요청에 포함하면 되며, 포함되지 않은 필드는 기존 값이 유지됩니다.")
    @PatchMapping(value = "/{moduleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuleSaveResponseDto> updateModule(@PathVariable Long moduleId,
        @RequestPart(value = "moduleDto", required = false) ModuleSaveDto moduleDto,
        @RequestPart(value = "topImage", required = false) MultipartFile topImageFile,
        @RequestPart(value = "isoImage", required = false) MultipartFile isoImageFile) {

        ModuleSaveResponseDto response = moduleService.updateModule(moduleId, moduleDto, topImageFile,
            isoImageFile);
        return ResponseEntity.ok(response);
    }
}
