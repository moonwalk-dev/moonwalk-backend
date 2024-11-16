package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.moonwalk.moonwalk_api.dto.mood.MoodResponseDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodListResponseDto;
import kr.moonwalk.moonwalk_api.service.MoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
