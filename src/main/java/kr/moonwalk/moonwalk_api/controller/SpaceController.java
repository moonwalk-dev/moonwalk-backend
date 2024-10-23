package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import kr.moonwalk.moonwalk_api.dto.space.CategoriesSpacesResponseDto;
import kr.moonwalk.moonwalk_api.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    @Operation(summary = "카테고리 별 공간 조회")
    @GetMapping("/spaces")
    public ResponseEntity<CategoriesSpacesResponseDto> getSpacesByCategories() {
        List<String> categoryNames = List.of("업무공간", "회의공간", "커뮤니티 공간", "시설공간", "기타공간");
        CategoriesSpacesResponseDto response = spaceService.getSpacesByCategories(categoryNames);

        return ResponseEntity.ok(response);
    }
}
