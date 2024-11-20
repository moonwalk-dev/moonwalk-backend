package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleAddDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleAddResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleDetailResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleListResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleSearchResultDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectCreateResponseDto;
import kr.moonwalk.moonwalk_api.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectCreateResponseDto> createProject(
        @RequestParam("estimateId") Long estimateId,
        @RequestPart(value = "blueprintImage", required = false) MultipartFile blueprintImage) {

        ProjectCreateResponseDto response = projectService.createProject(estimateId, blueprintImage);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "카테고리 별 마이모듈 리스트 조회",
        description = "프로젝트 ID를 경로로 받고, 선택된 카테고리 ID들로 마이모듈 리스트를 필터링합니다.")
    @GetMapping("/{projectId}/myModules")
    public ResponseEntity<MyModuleListResponseDto> getFilteredMyModules(
        @PathVariable Long projectId,
        @RequestParam(required = false) List<Long> categoryIds) {

        MyModuleListResponseDto response = projectService.getFilteredMyModules(projectId, categoryIds);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모듈 이름으로 마이모듈 리스트 검색")
    @GetMapping("/{projectId}/myModules/search")
    public ResponseEntity<MyModuleSearchResultDto> searchMyModulesByName(
        @PathVariable Long projectId,
        @RequestParam("query") String query) {
        MyModuleSearchResultDto response = projectService.searchMyModulesByName(projectId, query);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "마이 모듈 추가", description = "마이 모듈에 없으면 새로운 모듈을 추가하며, 이미 존재하는 경우 모듈의 수량을 업데이트합니다.")
    @PostMapping("/{projectId}")
    public ResponseEntity<MyModuleAddResponseDto> addModule(@PathVariable Long projectId,
        @Valid @RequestBody MyModuleAddDto myModuleAddDto) {

        MyModuleAddResponseDto response = projectService.addModule(projectId, myModuleAddDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "마이모듈 항목 삭제", description = "해당 마이모듈 항목을 삭제합니다.")
    @DeleteMapping("/{projectId}/myModules/{myModuleId}")
    public ResponseEntity<Void> deleteMyModule(@PathVariable Long projectId, @PathVariable Long myModuleId) {
        projectService.deleteMyModule(projectId, myModuleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모듈 배치")
    @PostMapping("/{projectId}/boards/{moduleId}")
    public ResponseEntity<ModulePlaceResponseDto> placeModule(@PathVariable Long projectId, @PathVariable Long moduleId,
        @Valid @RequestBody ModulePlaceDto modulePlaceDto) {

        ModulePlaceResponseDto response = projectService.placeModule(projectId, moduleId, modulePlaceDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "마이모듈 항목 상세 조회")
    @GetMapping("/{projectId}/myModules/{myModuleId}")
    public ResponseEntity<MyModuleDetailResponseDto> getInfoMyModule(@PathVariable Long projectId, @PathVariable Long myModuleId) {
        MyModuleDetailResponseDto response = projectService.getInfoMyModule(projectId, myModuleId);
        return ResponseEntity.ok(response);
    }
}
