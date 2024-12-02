package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import kr.moonwalk.moonwalk_api.dto.guide.CategoryGuidesResponseDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideResponseDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideSaveDto;
import kr.moonwalk.moonwalk_api.dto.guide.GuideSaveResponseDto;
import kr.moonwalk.moonwalk_api.service.GuideService;
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
@RequestMapping("/api/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @Operation(summary = "카테고리 별 오피스가이드 조회")
    @GetMapping
    public ResponseEntity<CategoryGuidesResponseDto> getGuidesByCategory(
        @RequestParam String categoryName) {

        CategoryGuidesResponseDto response = guideService.getGuidesByCategoryName(categoryName);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 전용 오피스가이드 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GuideSaveResponseDto> createGuide(
        @RequestPart("guide") GuideSaveDto guide,
        @RequestPart("coverImage") @Parameter(description = "Cover image file") MultipartFile coverImageFile,
        @RequestPart("detailImages") @Parameter(description = "Detail image files") List<MultipartFile> detailImageFiles) {

        GuideSaveResponseDto response = guideService.saveGuide(guide, coverImageFile,
            detailImageFiles);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 전용 오피스가이드 삭제")
    @DeleteMapping("/{guideId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGuide(@PathVariable Long guideId) {

        guideService.deleteGuide(guideId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "관리자 전용 오피스가이드 수정", description = "변경하고자 하는 필드만 요청에 포함하면 되며, 포함되지 않은 필드는 기존 값이 유지됩니다.")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GuideSaveResponseDto> updateGuide(@PathVariable Long id,
        @RequestPart(value = "guide", required = false) GuideSaveDto guideDto,
        @RequestPart(value = "coverImage", required = false) MultipartFile coverImageFile,
        @RequestPart(value = "detailImages", required = false) List<MultipartFile> detailImageFiles) {

        GuideSaveResponseDto response = guideService.updateGuide(id, guideDto, coverImageFile,
            detailImageFiles);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "특정 오피스가이드 조회")
    @GetMapping("/{guideId}")
    public ResponseEntity<GuideResponseDto> getGuideInfo(@PathVariable Long guideId) {

        GuideResponseDto response = guideService.getInfo(guideId);

        return ResponseEntity.ok(response);
    }
}
