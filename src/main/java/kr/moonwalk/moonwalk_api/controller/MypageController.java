package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.moonwalk.moonwalk_api.dto.project.ProjectInfoListDto;
import kr.moonwalk.moonwalk_api.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageController {

    private final MypageService mypageService;

    @Operation(summary = "마이페이지 프로젝트 리스트 조회")
    @GetMapping("/projects")
    public ResponseEntity<ProjectInfoListDto> getModulePositions() {

        ProjectInfoListDto response = mypageService.getProjects();

        return ResponseEntity.ok(response);
    }
}
