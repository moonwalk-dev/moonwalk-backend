package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import kr.moonwalk.moonwalk_api.dto.category.CategoryGuidesResponseDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideSaveDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideSaveResponseDto;
import kr.moonwalk.moonwalk_api.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @Operation(summary = "카테고리 별 공간 조회")
    @GetMapping
    public ResponseEntity<CategoryGuidesResponseDto> getGuidesByCategory(
        @RequestParam(required = true) Long categoryId) {

        CategoryGuidesResponseDto response = guideService.getGuidesByCategoryId(categoryId);
        return ResponseEntity.ok(response);

    }

    @Operation(summary = "공간 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GuideSaveResponseDto> createGuide(
        @RequestPart("guide") GuideSaveDto guide,
        @RequestPart("coverImage") @Parameter(description = "Cover image file") MultipartFile coverImageFile,
        @RequestPart("detailImages") @Parameter(description = "Detail image files") List<MultipartFile> detailImageFiles) {

        GuideSaveResponseDto response = guideService.saveGuide(guide, coverImageFile, detailImageFiles);
        return ResponseEntity.ok(response);
    }


}
