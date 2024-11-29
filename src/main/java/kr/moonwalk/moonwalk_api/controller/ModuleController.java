package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import kr.moonwalk.moonwalk_api.dto.module.CategoriesModulesResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleSearchResultDto;
import kr.moonwalk.moonwalk_api.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @Operation(summary = "카테고리 별 모듈 조회")
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
}
