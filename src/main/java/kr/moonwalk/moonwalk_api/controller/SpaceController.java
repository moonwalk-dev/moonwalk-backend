package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.moonwalk.moonwalk_api.dto.category.CategorySpacesResponseDto;
import kr.moonwalk.moonwalk_api.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    @Operation(summary = "카테고리 별 공간 조회")
    @GetMapping
    public ResponseEntity<CategorySpacesResponseDto> getSpacesByCategory(
        @RequestParam(required = true) Long categoryId) {

        CategorySpacesResponseDto response = spaceService.getSpacesByCategoryId(categoryId);
        return ResponseEntity.ok(response);

    }
}
