package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.moonwalk.moonwalk_api.dto.category.CategoryListResponseDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySaveDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySaveResponseDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySpacesResponseDto;
import kr.moonwalk.moonwalk_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 별 공간 조회")
    @GetMapping("/{categoryId}/spaces")
    public ResponseEntity<CategorySpacesResponseDto> getSpacesByCategory(
        @PathVariable Long categoryId) {

        CategorySpacesResponseDto response = categoryService.getSpacesById(categoryId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategorySaveResponseDto> create(CategorySaveDto categorySaveDto) {

        CategorySaveResponseDto response = categoryService.create(categorySaveDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CategoryListResponseDto> getAllCategories() {

        CategoryListResponseDto response = categoryService.getAllCategories();

        return ResponseEntity.ok(response);
    }
}
