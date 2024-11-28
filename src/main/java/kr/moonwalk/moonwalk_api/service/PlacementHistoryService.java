package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Module;
import kr.moonwalk.moonwalk_api.domain.MyModule;
import kr.moonwalk.moonwalk_api.domain.Project;
import kr.moonwalk.moonwalk_api.domain.ProjectModule;
import kr.moonwalk.moonwalk_api.domain.ProjectPlacementHistory;
import kr.moonwalk.moonwalk_api.dto.project.ModulePositionDto;
import kr.moonwalk.moonwalk_api.dto.project.UndoPlaceResponseDto;
import kr.moonwalk.moonwalk_api.exception.notfound.ModuleNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ProjectModuleNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ProjectNotFoundException;
import kr.moonwalk.moonwalk_api.repository.ModuleRepository;
import kr.moonwalk.moonwalk_api.repository.MyModuleRepository;
import kr.moonwalk.moonwalk_api.repository.ProjectModuleRepository;
import kr.moonwalk.moonwalk_api.repository.ProjectPlacementHistoryRepository;
import kr.moonwalk.moonwalk_api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlacementHistoryService {

    private final ProjectPlacementHistoryRepository historyRepository;
    private final ProjectModuleRepository projectModuleRepository;
    private final MyModuleRepository myModuleRepository;
    private final ProjectRepository projectRepository;
    private final ModuleRepository moduleRepository;

    @Transactional
    public void saveHistory(Long projectId, Long moduleId, Long projectModuleId, String actionType, int positionX, int positionY, int angle) {
        ProjectPlacementHistory history = new ProjectPlacementHistory(projectId, moduleId, projectModuleId, positionX, positionY, angle, actionType);
        historyRepository.save(history);
    }

    @Transactional
    public UndoPlaceResponseDto undoLastPlacement(Long projectId) {
        ProjectPlacementHistory lastHistory = historyRepository.findTopByProjectIdOrderByTimestampDesc(projectId)
            .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트에 대한 히스토리가 없습니다."));

        String actionType = lastHistory.getActionType();
        Long moduleId = lastHistory.getModuleId();
        Long projectModuleId = lastHistory.getProjectModuleId();
        UndoPlaceResponseDto responseDto;

        switch (actionType) {
            case "ADD":
                responseDto = undoAddPlacement(projectId, moduleId, projectModuleId, lastHistory);
                break;
            case "UPDATE":
                responseDto = undoUpdatePlacement(projectId, moduleId, projectModuleId, lastHistory);
                break;
            case "DELETE":
                responseDto = undoDeletePlacement(projectId, moduleId, projectModuleId, lastHistory);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 작업 유형: " + actionType);
        }

        historyRepository.delete(lastHistory);

        return responseDto;
    }

    private UndoPlaceResponseDto undoAddPlacement(Long projectId, Long moduleId, Long projectModuleId, ProjectPlacementHistory history) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));

        ProjectModule lastPlacement = projectModuleRepository.findById(projectModuleId)
            .orElseThrow(() -> new ProjectModuleNotFoundException("배치된 모듈을 찾을 수 없습니다."));

        MyModule myModule = myModuleRepository.findByProjectAndModule(project, module)
            .orElseThrow(() -> new ModuleNotFoundException("마이모듈을 찾을 수 없습니다."));

        myModule.decrementUsedQuantity();
        myModuleRepository.save(myModule);

        lastPlacement.markAsDeleted(true);
        projectModuleRepository.save(lastPlacement);

        project.updatePlacedTotalPrice();
        projectRepository.save(project);

        List<ModulePositionDto> updatedPositions = project.getProjectModules().stream()
            .filter(pm -> !pm.isDeleted())
            .map(pm -> new ModulePositionDto(pm.getId(), pm.getModule().getTopImage().getImageUrl(),
                pm.getPositionX(), pm.getPositionY(), pm.getAngle()))
            .collect(Collectors.toList());

        return new UndoPlaceResponseDto(
            projectId, myModule.getId(),
            myModule.getUsedQuantity(),
            updatedPositions
        );
    }

    private UndoPlaceResponseDto undoUpdatePlacement(Long projectId, Long moduleId, Long projectModuleId, ProjectPlacementHistory history) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));

        MyModule myModule = myModuleRepository.findByProjectAndModule(project, module)
            .orElseThrow(() -> new ModuleNotFoundException("마이모듈을 찾을 수 없습니다."));

        ProjectModule projectModule = projectModuleRepository.findById(projectModuleId)
            .orElseThrow(() -> new ProjectModuleNotFoundException("배치된 모듈을 찾을 수 없습니다."));

        projectModule.updatePosition(history.getPositionX(), history.getPositionY(), history.getAngle());
        projectModuleRepository.save(projectModule);

        List<ModulePositionDto> updatedPositions = project.getProjectModules().stream()
            .filter(pm -> !pm.isDeleted())
            .map(pm -> new ModulePositionDto(pm.getId(), pm.getModule().getTopImage().getImageUrl(),
                pm.getPositionX(), pm.getPositionY(), pm.getAngle()))
            .collect(Collectors.toList());

        return new UndoPlaceResponseDto(
            projectId, myModule.getId(),
            myModule.getUsedQuantity(),
            updatedPositions
        );
    }

    private UndoPlaceResponseDto undoDeletePlacement(Long projectId, Long moduleId, Long projectModuleId, ProjectPlacementHistory history) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));

        MyModule myModule = myModuleRepository.findByProjectAndModule(project, module)
            .orElseThrow(() -> new ModuleNotFoundException("마이모듈을 찾을 수 없습니다."));

        myModule.incrementUsedQuantity();
        myModuleRepository.save(myModule);

        ProjectModule projectModule = projectModuleRepository.findById(projectModuleId)
            .orElseThrow(() -> new ProjectModuleNotFoundException("배치된 모듈을 찾을 수 없습니다."));

        projectModule.markAsDeleted(false);
        projectModuleRepository.save(projectModule);

        project.updatePlacedTotalPrice();
        projectRepository.save(project);


        List<ModulePositionDto> updatedPositions = project.getProjectModules().stream()
            .filter(pm -> !pm.isDeleted())
            .map(pm -> new ModulePositionDto(pm.getId(), pm.getModule().getTopImage().getImageUrl(),
                pm.getPositionX(), pm.getPositionY(), pm.getAngle()))
            .collect(Collectors.toList());

        return new UndoPlaceResponseDto(
            projectId, myModule.getId(),
            myModule.getUsedQuantity(),
            updatedPositions
        );
    }

}

