package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.moonwalk.moonwalk_api.dto.module.CategoryModulesResponseDto;
import kr.moonwalk.moonwalk_api.dto.module.ModuleResponseDto;
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
    public ResponseEntity<CategoryModulesResponseDto> getModulesByCategory(
        @RequestParam(required = true) Long categoryId) {

        CategoryModulesResponseDto response = moduleService.getModulesByCategoryId(categoryId);
        return ResponseEntity.ok(response);

    }

    @Operation(summary = "특정 모듈 조회")
    @GetMapping("/{moduleId}")
    public ResponseEntity<ModuleResponseDto> getModuleInfo(@PathVariable Long moduleId) {

        ModuleResponseDto response = moduleService.getInfo(moduleId);

        return ResponseEntity.ok(response);
    }
}
