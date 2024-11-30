package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import kr.moonwalk.moonwalk_api.dto.mypage.ProjectInfoListDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectCreateDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectCreateResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectPriceResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectSaveDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectSaveResponseDto;
import kr.moonwalk.moonwalk_api.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "마이페이지에서 생성할 땐 견적 id가 필요 없고, 피그마(모듈 선택하기-견적 확인하기_모듈 클릭/마우스 오버 시)에서 생성 시 견적 id만 필요")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectCreateResponseDto> createProject(
        @RequestPart("projectCreateDto") ProjectCreateDto projectCreateDto,
        @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
        @RequestPart(value = "blueprintImage", required = false) MultipartFile blueprintImage) {

        ProjectCreateResponseDto response = projectService.createProject(projectCreateDto,
            mainImage, blueprintImage);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로젝트 총 금액 조회")
    @GetMapping("/{projectId}/category-prices")
    public ResponseEntity<ProjectPriceResponseDto> getCategoryPrices(@PathVariable Long projectId) {
        ProjectPriceResponseDto response = projectService.getProjectPriceDetails(projectId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로젝트 저장")
    @PostMapping(path = "/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectSaveResponseDto> save(@PathVariable Long projectId,
        @RequestPart("projectSaveDto") ProjectSaveDto projectSaveDto,
        @RequestPart(value = "coverImage", required = false) @Parameter(description = "Cover image file") MultipartFile coverImageFile) {

        ProjectSaveResponseDto response = projectService.save(projectId, projectSaveDto,
            coverImageFile);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로젝트 삭제")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long projectId) {

        projectService.deleteProject(projectId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "마이페이지 프로젝트 리스트 조회")
    @GetMapping
    public ResponseEntity<ProjectInfoListDto> getProjects() {

        ProjectInfoListDto response = projectService.getProjects();

        return ResponseEntity.ok(response);
    }

}
