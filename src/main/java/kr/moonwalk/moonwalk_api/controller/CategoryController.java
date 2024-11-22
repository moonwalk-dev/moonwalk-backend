package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.moonwalk.moonwalk_api.dto.category.CategoryListResponseDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySaveDto;
import kr.moonwalk.moonwalk_api.dto.category.CategorySaveResponseDto;
import kr.moonwalk.moonwalk_api.dto.category.CategoryUpdateDto;
import kr.moonwalk.moonwalk_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "카테고리 생성")
    @PostMapping
    public ResponseEntity<CategorySaveResponseDto> create(@Valid @RequestBody CategorySaveDto categorySaveDto) {

        CategorySaveResponseDto response = categoryService.create(categorySaveDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 카테고리 리스트 조회")
    @GetMapping
    public ResponseEntity<CategoryListResponseDto> getAllCategories() {

        CategoryListResponseDto response = categoryService.getAllCategories();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "카테고리 수정")
    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategorySaveResponseDto> update(@PathVariable Long categoryId,
        @Valid @RequestBody CategoryUpdateDto updateDto) {

        CategorySaveResponseDto response = categoryService.updateCategory(categoryId, updateDto);

        return ResponseEntity.ok(response);
    }
}
