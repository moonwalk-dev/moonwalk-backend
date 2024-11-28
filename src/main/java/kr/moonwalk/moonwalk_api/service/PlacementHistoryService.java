package kr.moonwalk.moonwalk_api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
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

    private final int MAX_HISTORY = 10;
    private final Map<Long, Stack<ProjectPlacementHistory>> undoStacks = new HashMap<>();
    private final Map<Long, Stack<ProjectPlacementHistory>> redoStacks = new HashMap<>();

    @Transactional
    public void saveHistory(Project project, Module module, ProjectModule projectModule, String actionType, int positionX, int positionY, int angle) {
        Long projectId = project.getId();
        undoStacks.putIfAbsent(projectId, new Stack<>());
        redoStacks.putIfAbsent(projectId, new Stack<>());

        redoStacks.get(projectId).clear();

        Stack<ProjectPlacementHistory> undoStack = undoStacks.get(projectId);
        if (undoStack.size() >= MAX_HISTORY) {
            undoStack.remove(0);
        }

        ProjectPlacementHistory history = new ProjectPlacementHistory(
            project, module, projectModule, positionX, positionY, angle, actionType
        );
        historyRepository.save(history);
        undoStack.push(history);
    }

    @Transactional
    public UndoPlaceResponseDto undoLastPlacement(Long projectId) {
        Stack<ProjectPlacementHistory> undoStack = undoStacks.getOrDefault(projectId, new Stack<>());
        Stack<ProjectPlacementHistory> redoStack = redoStacks.getOrDefault(projectId, new Stack<>());

        if (undoStack.isEmpty()) {
            throw new IllegalStateException("더 이상 되돌릴 작업이 없습니다.");
        }

        ProjectPlacementHistory lastHistory = undoStack.pop();
        redoStack.push(lastHistory);

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        String actionType = lastHistory.getActionType();
        Module module = lastHistory.getModule();
        ProjectModule projectModule = lastHistory.getProjectModule();
        UndoPlaceResponseDto responseDto;

        switch (actionType) {
            case "ADD":
                responseDto = undoAddPlacement(project, module, projectModule);
                break;
            case "UPDATE":
                responseDto = undoUpdatePlacement(project, module, projectModule, lastHistory);
                break;
            case "DELETE":
                responseDto = undoDeletePlacement(project, module, projectModule);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 작업 유형: " + actionType);
        }

        return responseDto;
    }

    @Transactional
    public UndoPlaceResponseDto redoLastPlacement(Long projectId) {
        Stack<ProjectPlacementHistory> undoStack = undoStacks.getOrDefault(projectId, new Stack<>());
        Stack<ProjectPlacementHistory> redoStack = redoStacks.getOrDefault(projectId, new Stack<>());

        if (redoStack.isEmpty()) {
            throw new IllegalStateException("다시 실행할 작업이 없습니다.");
        }

        ProjectPlacementHistory lastRedoHistory = redoStack.pop();
        undoStack.push(lastRedoHistory);

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        String actionType = lastRedoHistory.getActionType();
        Module module = lastRedoHistory.getModule();
        ProjectModule projectModule = lastRedoHistory.getProjectModule();
        UndoPlaceResponseDto responseDto;

        switch (actionType) {
            case "ADD":
                responseDto = redoAddPlacement(project, module, projectModule);
                break;
            case "UPDATE":
                responseDto = redoUpdatePlacement(project, module, projectModule, lastRedoHistory);
                break;
            case "DELETE":
                responseDto = redoDeletePlacement(project, module, projectModule);
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 작업 유형: " + actionType);
        }

        return responseDto;
    }

    private UndoPlaceResponseDto undoAddPlacement(Project project, Module module, ProjectModule projectModule) {
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
        projectModule.updatePosition(history.getPositionX(), history.getPositionY(), history.getAngle());
        projectModuleRepository.save(projectModule);

        List<ModulePositionDto> updatedPositions = project.getProjectModules().stream()
            .filter(pm -> !pm.isDeleted())
            .map(pm -> new ModulePositionDto(pm.getId(), pm.getModule().getTopImage().getImageUrl(),
                pm.getPositionX(), pm.getPositionY(), pm.getAngle()))
            .collect(Collectors.toList());

        return new UndoPlaceResponseDto(
            project.getId(), module.getId(),
            0,
            updatedPositions
        );
    }

    private UndoPlaceResponseDto undoDeletePlacement(Project project, Module module, ProjectModule projectModule) {
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

    private UndoPlaceResponseDto redoAddPlacement(Project project, Module module, ProjectModule projectModule) {
        projectModule.markAsDeleted(false);
        projectModuleRepository.save(projectModule);

        MyModule myModule = myModuleRepository.findByProjectAndModule(project, module)
            .orElseThrow(() -> new ModuleNotFoundException("마이모듈을 찾을 수 없습니다."));

        myModule.incrementUsedQuantity();
        myModuleRepository.save(myModule);

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

    private UndoPlaceResponseDto redoUpdatePlacement(Project project, Module module, ProjectModule projectModule, ProjectPlacementHistory history) {
        projectModule.updatePosition(history.getPositionX(), history.getPositionY(), history.getAngle());
        projectModuleRepository.save(projectModule);

        List<ModulePositionDto> updatedPositions = project.getProjectModules().stream()
            .filter(pm -> !pm.isDeleted())
            .map(pm -> new ModulePositionDto(pm.getId(), pm.getModule().getTopImage().getImageUrl(),
                pm.getPositionX(), pm.getPositionY(), pm.getAngle()))
            .collect(Collectors.toList());

        return new UndoPlaceResponseDto(
            project.getId(), module.getId(),
            0,
            updatedPositions
        );
    }

    private UndoPlaceResponseDto redoDeletePlacement(Project project, Module module, ProjectModule projectModule) {
        projectModule.markAsDeleted(true);
        projectModuleRepository.save(projectModule);

        MyModule myModule = myModuleRepository.findByProjectAndModule(project, module)
            .orElseThrow(() -> new ModuleNotFoundException("마이모듈을 찾을 수 없습니다."));

        myModule.decrementUsedQuantity();
        myModuleRepository.save(myModule);

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

    public void clearHistory(Long projectId) {
        undoStacks.remove(projectId);
        redoStacks.remove(projectId);
    }
}

