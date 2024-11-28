package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.List;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceUpdateResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePositionListDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleAddDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleAddResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleDetailResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleListResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleSearchResultDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectBlueprintResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectCreateDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectCreateResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectPriceResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectSaveDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectSaveResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.UndoPlaceResponseDto;
import kr.moonwalk.moonwalk_api.service.PlacementHistoryService;
import kr.moonwalk.moonwalk_api.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    private final PlacementHistoryService placementHistoryService;

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


    @Operation(summary = "카테고리 별 마이모듈 리스트 조회", description = "프로젝트 ID를 경로로 받고, 선택된 카테고리 ID들로 마이모듈 리스트를 필터링합니다.")
    @GetMapping("/{projectId}/myModules")
    public ResponseEntity<MyModuleListResponseDto> getFilteredMyModules(
        @PathVariable Long projectId, @RequestParam(required = false) List<Long> categoryIds) {

        MyModuleListResponseDto response = projectService.getFilteredMyModules(projectId,
            categoryIds);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모듈 이름으로 마이모듈 리스트 검색")
    @GetMapping("/{projectId}/myModules/search")
    public ResponseEntity<MyModuleSearchResultDto> searchMyModulesByName(
        @PathVariable Long projectId, @RequestParam("query") String query) {
        MyModuleSearchResultDto response = projectService.searchMyModulesByName(projectId, query);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "마이 모듈 추가 및 수정", description = "마이 모듈에 없으면 새로운 모듈을 추가하며, 이미 존재하는 경우 모듈의 수량을 업데이트합니다.")
    @PostMapping("/{projectId}/myModules")
    public ResponseEntity<MyModuleAddResponseDto> addModule(@PathVariable Long projectId,
        @Valid @RequestBody MyModuleAddDto myModuleAddDto) {

        MyModuleAddResponseDto response = projectService.addModule(projectId, myModuleAddDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "마이모듈 항목 삭제", description = "@@@@@ 마이모듈 삭제 시 배치된 모듈들도 삭제된다고 경고 띄워주면 좋을 것 같습니다.@@@@@")
    @DeleteMapping("/{projectId}/myModules/{myModuleId}")
    public ResponseEntity<Void> deleteMyModule(@PathVariable Long projectId,
        @PathVariable Long myModuleId) {
        projectService.deleteMyModule(projectId, myModuleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모듈 배치")
    @PostMapping("/{projectId}/boards/{moduleId}")
    public ResponseEntity<ModulePlaceResponseDto> placeModule(@PathVariable Long projectId,
        @PathVariable Long moduleId, @Valid @RequestBody ModulePlaceDto modulePlaceDto) {

        ModulePlaceResponseDto response = projectService.placeModule(projectId, moduleId,
            modulePlaceDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "마이모듈 항목 상세 조회")
    @GetMapping("/{projectId}/myModules/{myModuleId}")
    public ResponseEntity<MyModuleDetailResponseDto> getInfoMyModule(@PathVariable Long projectId,
        @PathVariable Long myModuleId) {
        MyModuleDetailResponseDto response = projectService.getInfoMyModule(projectId, myModuleId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모듈 배치 수정")
    @PatchMapping("/{projectId}/boards/{projectModuleId}")
    public ResponseEntity<ModulePlaceUpdateResponseDto> updatePlaceModule(
        @PathVariable Long projectId, @PathVariable Long projectModuleId,
        @Valid @RequestBody ModulePlaceDto modulePlaceDto) {

        ModulePlaceUpdateResponseDto response = projectService.updatePlaceModule(projectId,
            projectModuleId, modulePlaceDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배치된 모듈 삭제")
    @DeleteMapping("/{projectId}/boards/{placementId}")
    public ResponseEntity<Void> deletePlaceModule(
        @PathVariable Long projectId, @PathVariable Long placementId) {

        projectService.deletePlaceModule(projectId, placementId);

        return ResponseEntity.noContent().build();
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

    @Operation(summary = "프로젝트 도면 추가 및 변경")
    @PostMapping(path = "/{projectId}/blueprint", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectBlueprintResponseDto> addBlueprint(@PathVariable Long projectId,
        @RequestPart(value = "blueprintImage") @Parameter(description = "Cover image file") MultipartFile blueprintImageFile) {

        ProjectBlueprintResponseDto response = projectService.addOrUpdateBlueprint(projectId,
            blueprintImageFile);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로젝트 도면 삭제")
    @DeleteMapping(path = "/{projectId}/blueprint")
    public ResponseEntity<Void> deleteBlueprint(@PathVariable Long projectId) {

        projectService.deleteBlueprint(projectId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "프로젝트 모듈 포지션 리스트 조회")
    @GetMapping("/{projectId}/boards/")
    public ResponseEntity<ModulePositionListDto> getModulePositions(@PathVariable Long projectId) {

        ModulePositionListDto response = projectService.getModulePositions(projectId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로젝트 도면 이미지 조회")
    @GetMapping("/{projectId}/blueprint/")
    public ResponseEntity<ProjectBlueprintResponseDto> getBlueprintUrl(@PathVariable Long projectId) {

        ProjectBlueprintResponseDto response = projectService.getBlueprintUrl(projectId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로젝트 모듈 배치 되돌리기")
    @PostMapping(path = "/{projectId}/undo")
    public ResponseEntity<UndoPlaceResponseDto> undo(@PathVariable Long projectId) {

        UndoPlaceResponseDto response = placementHistoryService.undoLastPlacement(projectId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{projectId}/redo")
    public ResponseEntity<UndoPlaceResponseDto> redo(@PathVariable Long projectId) {
        UndoPlaceResponseDto response = placementHistoryService.redoLastPlacement(projectId);
        return ResponseEntity.ok(response);
    }
}
