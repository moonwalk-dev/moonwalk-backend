package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import kr.moonwalk.moonwalk_api.dto.mood.MoodSaveDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodSaveResponseDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodListResponseDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodResponseDto;
import kr.moonwalk.moonwalk_api.service.MoodService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/moods")
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;

    @Operation(summary = "전체 무드 조회")
    @GetMapping
    public ResponseEntity<MoodListResponseDto> getAllMoods() {

        MoodListResponseDto response = moodService.getAllMoods();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 무드 조회")
    @GetMapping("/{moodId}")
    public ResponseEntity<MoodResponseDto> getMoodInfo(@PathVariable Long moodId) {

        MoodResponseDto response = moodService.getInfo(moodId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 전용 무드 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MoodSaveResponseDto> createMood(
        @RequestPart("moodDto") MoodSaveDto moodDto,
        @RequestPart("coverImage") @Parameter(description = "Cover image file") MultipartFile coverImageFile,
        @RequestPart("detailImages") @Parameter(description = "Detail image files") List<MultipartFile> detailImageFiles) {

        MoodSaveResponseDto response = moodService.saveMood(moodDto, coverImageFile,
            detailImageFiles);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 전용 무드 삭제")
    @DeleteMapping("/{moodId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMood(@PathVariable Long moodId) {

        moodService.deleteMood(moodId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "관리자 전용 무드 수정")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MoodSaveResponseDto> updateMood(
        @PathVariable Long id,
        @RequestPart(value = "mood", required = false) MoodSaveDto moodDto,
        @RequestPart(value = "coverImage", required = false) MultipartFile coverImageFile) {

        MoodSaveResponseDto response = moodService.updateMood(id, moodDto, coverImageFile);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 전용 오피스 무드 상세이미지 추가")
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MoodResponseDto> addDetailImages(
        @PathVariable Long id,
        @RequestPart("detailImages") List<MultipartFile> detailImageFiles) {

        MoodResponseDto response = moodService.addDetailImages(id, detailImageFiles);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 전용 오피스 무드 상세이미지 삭제")
    @DeleteMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDetailImage(
        @PathVariable Long id,
        @PathVariable Long imageId) {

        moodService.deleteDetailImage(id, imageId);
        return ResponseEntity.noContent().build();
    }
}
