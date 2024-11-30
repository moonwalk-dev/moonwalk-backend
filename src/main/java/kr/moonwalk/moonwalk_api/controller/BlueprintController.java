package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import kr.moonwalk.moonwalk_api.dto.project.ProjectBlueprintResponseDto;
import kr.moonwalk.moonwalk_api.service.BlueprintService;
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
public class BlueprintController {

    private final BlueprintService blueprintService;

    @Operation(summary = "프로젝트 도면 추가 및 변경")
    @PostMapping(path = "/{projectId}/blueprint", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectBlueprintResponseDto> addBlueprint(@PathVariable Long projectId,
        @RequestPart(value = "blueprintImage") @Parameter(description = "Cover image file") MultipartFile blueprintImageFile) {

        ProjectBlueprintResponseDto response = blueprintService.addOrUpdateBlueprint(projectId,
            blueprintImageFile);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로젝트 도면 삭제")
    @DeleteMapping(path = "/{projectId}/blueprint")
    public ResponseEntity<Void> deleteBlueprint(@PathVariable Long projectId) {

        blueprintService.deleteBlueprint(projectId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "프로젝트 도면 이미지 조회")
    @GetMapping("/{projectId}/blueprint/")
    public ResponseEntity<ProjectBlueprintResponseDto> getBlueprintUrl(@PathVariable Long projectId) {

        ProjectBlueprintResponseDto response = blueprintService.getBlueprintUrl(projectId);

        return ResponseEntity.ok(response);
    }


}
