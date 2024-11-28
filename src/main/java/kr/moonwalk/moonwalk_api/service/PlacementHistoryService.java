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
    public void saveHistory(Project project, Module module, ProjectModule projectModule, String actionType, int positionX, int positionY, int angle) {
        ProjectPlacementHistory history = new ProjectPlacementHistory(project, module, projectModule, positionX, positionY, angle, actionType);
        historyRepository.save(history);
    }

    @Transactional
    public UndoPlaceResponseDto undoLastPlacement(Long projectId) {

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        ProjectPlacementHistory lastHistory = historyRepository.findTopByProjectOrderByTimestampDesc(project)
            .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트에 대한 히스토리가 없습니다."));

        String actionType = lastHistory.getActionType();
        Module module = lastHistory.getModule();
        ProjectModule projectModule = lastHistory.getProjectModule();
        UndoPlaceResponseDto responseDto;

        switch (actionType) {
            case "ADD":
                responseDto = undoAddPlacement(project, module, projectModule, lastHistory);
                break;
            case "UPDATE":
                responseDto = undoUpdatePlacement(project, module, projectModule, lastHistory);
                break;
            case "DELETE":
                responseDto = undoDeletePlacement(project, module, projectModule, lastHistory);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 작업 유형: " + actionType);
        }

        historyRepository.delete(lastHistory);

        return responseDto;
    }

    private UndoPlaceResponseDto undoAddPlacement(Project project, Module module, ProjectModule projectModule, ProjectPlacementHistory history) {

        MyModule myModule = myModuleRepository.findByProjectAndModule(project, module)
            .orElseThrow(() -> new ModuleNotFoundException("마이모듈을 찾을 수 없습니다."));

        myModule.decrementUsedQuantity();
        myModuleRepository.save(myModule);

        projectModule.markAsDeleted(true);
        projectModuleRepository.save(projectModule);

        project.updatePlacedTotalPrice();
        projectRepository.save(project);

        List<ModulePositionDto> updatedPositions = project.getProjectModules().stream()
            .filter(pm -> !pm.isDeleted())
            .map(pm -> new ModulePositionDto(pm.getId(), pm.getModule().getTopImage().getImageUrl(),
                pm.getPositionX(), pm.getPositionY(), pm.getAngle()))
            .collect(Collectors.toList());

        return new UndoPlaceResponseDto(
            project.getId(), myModule.getId(),
            myModule.getUsedQuantity(),
            updatedPositions
        );
    }

    private UndoPlaceResponseDto undoUpdatePlacement(Project project, Module module, ProjectModule projectModule, ProjectPlacementHistory history) {

        MyModule myModule = myModuleRepository.findByProjectAndModule(project, module)
            .orElseThrow(() -> new ModuleNotFoundException("마이모듈을 찾을 수 없습니다."));

        projectModule.updatePosition(history.getPositionX(), history.getPositionY(), history.getAngle());
        projectModuleRepository.save(projectModule);

        List<ModulePositionDto> updatedPositions = project.getProjectModules().stream()
            .filter(pm -> !pm.isDeleted())
            .map(pm -> new ModulePositionDto(pm.getId(), pm.getModule().getTopImage().getImageUrl(),
                pm.getPositionX(), pm.getPositionY(), pm.getAngle()))
            .collect(Collectors.toList());

        return new UndoPlaceResponseDto(
            project.getId(), myModule.getId(),
            myModule.getUsedQuantity(),
            updatedPositions
        );
    }

    private UndoPlaceResponseDto undoDeletePlacement(Project project, Module module, ProjectModule projectModule, ProjectPlacementHistory history) {
        MyModule myModule = myModuleRepository.findByProjectAndModule(project, module)
            .orElseThrow(() -> new ModuleNotFoundException("마이모듈을 찾을 수 없습니다."));

        myModule.incrementUsedQuantity();
        myModuleRepository.save(myModule);

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
            project.getId(), myModule.getId(),
            myModule.getUsedQuantity(),
            updatedPositions
        );
    }


    public void deleteByProjectAndModule(Project project, Module module) {
        historyRepository.deleteByProjectAndModule(project, module);
    }
}

