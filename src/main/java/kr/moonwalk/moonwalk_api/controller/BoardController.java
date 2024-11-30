package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceUpdateResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePositionListDto;
import kr.moonwalk.moonwalk_api.dto.project.UndoPlaceResponseDto;
import kr.moonwalk.moonwalk_api.service.BoardService;
import kr.moonwalk.moonwalk_api.service.PlacementHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class BoardController {

    private final BoardService boardService;
    private final PlacementHistoryService placementHistoryService;

    @Operation(summary = "모듈 배치")
    @PostMapping("/{projectId}/boards/{moduleId}")
    public ResponseEntity<ModulePlaceResponseDto> placeModule(@PathVariable Long projectId,
        @PathVariable Long moduleId, @Valid @RequestBody ModulePlaceDto modulePlaceDto) {

        ModulePlaceResponseDto response = boardService.placeModule(projectId, moduleId,
            modulePlaceDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모듈 배치 수정")
    @PatchMapping("/{projectId}/boards/{projectModuleId}")
    public ResponseEntity<ModulePlaceUpdateResponseDto> updatePlaceModule(
        @PathVariable Long projectId, @PathVariable Long projectModuleId,
        @Valid @RequestBody ModulePlaceDto modulePlaceDto) {

        ModulePlaceUpdateResponseDto response = boardService.updatePlaceModule(projectId,
            projectModuleId, modulePlaceDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배치된 모듈 삭제")
    @DeleteMapping("/{projectId}/boards/{placementId}")
    public ResponseEntity<Void> deletePlaceModule(
        @PathVariable Long projectId, @PathVariable Long placementId) {

        boardService.deletePlaceModule(projectId, placementId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "프로젝트 모듈 포지션 리스트 조회")
    @GetMapping("/{projectId}/boards")
    public ResponseEntity<ModulePositionListDto> getModulePositions(@PathVariable Long projectId) {

        ModulePositionListDto response = boardService.getModulePositions(projectId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로젝트 모듈 배치 되돌리기")
    @PostMapping(path = "/{projectId}/boards/undo")
    public ResponseEntity<UndoPlaceResponseDto> undo(@PathVariable Long projectId) {

        UndoPlaceResponseDto response = placementHistoryService.undoLastPlacement(projectId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로젝트 모듈 배치 다시 실행")
    @PostMapping("/{projectId}/boards/redo")
    public ResponseEntity<UndoPlaceResponseDto> redo(@PathVariable Long projectId) {
        UndoPlaceResponseDto response = placementHistoryService.redoLastPlacement(projectId);
        return ResponseEntity.ok(response);
    }
}
