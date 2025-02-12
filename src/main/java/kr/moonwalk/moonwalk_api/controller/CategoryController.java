package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import kr.moonwalk.moonwalk_api.dto.category.CategoryListResponseDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySaveDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySaveResponseDto;
import kr.moonwalk.moonwalk_api.dto.category.CategoryUpdateDto;
import kr.moonwalk.moonwalk_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "관리자 전용 오피스가이드 카테고리 생성")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/office-guide")
    public ResponseEntity<CategorySaveResponseDto> createOfficeCategory(@Valid @RequestBody CategorySaveDto categorySaveDto) {

        CategorySaveResponseDto response = categoryService.create(categorySaveDto, Type.TYPE_OFFICE);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 전용 모듈 카테고리 생성")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/module-select")
    public ResponseEntity<CategorySaveResponseDto> createModuleCategory(@Valid @RequestBody CategorySaveDto categorySaveDto) {

        CategorySaveResponseDto response = categoryService.create(categorySaveDto, Type.TYPE_MODULE);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "오피스가이드 카테고리 리스트 조회")
    @GetMapping("/office-guide")
    public ResponseEntity<CategoryListResponseDto> getOfficeGuideCategories() {

        CategoryListResponseDto response = categoryService.getOfficeGuideCategories();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모듈 카테고리 리스트 조회")
    @GetMapping("/module-select")
    public ResponseEntity<CategoryListResponseDto> getModuleCategories() {

        CategoryListResponseDto response = categoryService.getModuleCategories();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 전용 오피스가이드 카테고리 이름 수정")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/office-guide/{categoryId}")
    public ResponseEntity<CategorySaveResponseDto> updateOfficeGuideCategory(@PathVariable Long categoryId,
        @Valid @RequestBody CategoryUpdateDto updateDto) {

        CategorySaveResponseDto response = categoryService.updateOfficeGuideCategory(categoryId, updateDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 전용 모듈 카테고리 이름 수정")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/module-select/{categoryId}")
    public ResponseEntity<CategorySaveResponseDto> updateModuleCategory(@PathVariable Long categoryId,
        @Valid @RequestBody CategoryUpdateDto updateDto) {

        CategorySaveResponseDto response = categoryService.updateModuleCategory(categoryId, updateDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 전용 오피스가이드 카테고리 삭제")
    @DeleteMapping("/office-guide/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOfficeGuideCategory(@PathVariable Long categoryId) {

        categoryService.deleteOfficeGuideCategory(categoryId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "관리자 전용 모듈 카테고리 삭제")
    @DeleteMapping("/module-select/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteModuleCategory(@PathVariable Long categoryId) {

        categoryService.deleteModuleCategory(categoryId);

        return ResponseEntity.noContent().build();
    }
}
