package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleAddDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleAddResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleDetailResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleListResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleSearchResultDto;
import kr.moonwalk.moonwalk_api.service.MyModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class MyModuleController {

    private final MyModuleService myModuleService;

    @Operation(summary = "카테고리 별 마이모듈 리스트 조회", description = "프로젝트 ID를 경로로 받고, 선택된 카테고리 이름들로 마이모듈 리스트를 필터링합니다.")
    @GetMapping("/{projectId}/myModules")
    public ResponseEntity<MyModuleListResponseDto> getFilteredMyModules(
        @PathVariable Long projectId, @RequestParam(required = false) List<String> categoryNames) {

        MyModuleListResponseDto response = myModuleService.getFilteredMyModules(projectId,
            categoryNames);

        return ResponseEntity.ok(response);
    }
    @Operation(summary = "모듈 이름으로 마이모듈 리스트 검색")
    @GetMapping("/{projectId}/myModules/search")
    public ResponseEntity<MyModuleSearchResultDto> searchMyModulesByName(
        @PathVariable Long projectId, @RequestParam("query") String query) {
        MyModuleSearchResultDto response = myModuleService.searchMyModulesByName(projectId, query);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "마이 모듈 추가 및 수정", description = "마이 모듈에 없으면 새로운 모듈을 추가하며, 이미 존재하는 경우 모듈의 수량을 업데이트합니다.")
    @PostMapping("/{projectId}/myModules")
    public ResponseEntity<MyModuleAddResponseDto> addModule(@PathVariable Long projectId,
        @Valid @RequestBody MyModuleAddDto myModuleAddDto) {

        MyModuleAddResponseDto response = myModuleService.addModule(projectId, myModuleAddDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "마이모듈 항목 삭제", description = "@@@@@ 마이모듈 삭제 시 배치된 모듈들도 삭제된다고 경고 띄워주면 좋을 것 같습니다.@@@@@")
    @DeleteMapping("/{projectId}/myModules/{myModuleId}")
    public ResponseEntity<Void> deleteMyModule(@PathVariable Long projectId,
        @PathVariable Long myModuleId) {
        myModuleService.deleteMyModule(projectId, myModuleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "마이모듈 항목 상세 조회")
    @GetMapping("/{projectId}/myModules/{myModuleId}")
    public ResponseEntity<MyModuleDetailResponseDto> getInfoMyModule(@PathVariable Long projectId,
        @PathVariable Long myModuleId) {
        MyModuleDetailResponseDto response = myModuleService.getInfoMyModule(projectId, myModuleId);
        return ResponseEntity.ok(response);
    }

}
