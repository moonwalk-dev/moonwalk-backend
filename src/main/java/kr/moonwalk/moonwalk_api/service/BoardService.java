package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.dto.project.CanvasDto;
import kr.moonwalk.moonwalk_api.domain.Module;
import kr.moonwalk.moonwalk_api.domain.MyModule;
import kr.moonwalk.moonwalk_api.domain.Project;
import kr.moonwalk.moonwalk_api.domain.ProjectModule;
import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.dto.project.CanvasResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceUpdateResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePositionDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePositionListDto;
import kr.moonwalk.moonwalk_api.exception.notfound.CartNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ModuleNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ProjectModuleNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ProjectNotFoundException;
import kr.moonwalk.moonwalk_api.repository.ModuleRepository;
import kr.moonwalk.moonwalk_api.repository.MyModuleRepository;
import kr.moonwalk.moonwalk_api.repository.ProjectModuleRepository;
import kr.moonwalk.moonwalk_api.repository.ProjectRepository;
import kr.moonwalk.moonwalk_api.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final ProjectRepository projectRepository;
    private final MyModuleRepository myModuleRepository;
    private final ModuleRepository moduleRepository;
    private final ProjectModuleRepository projectModuleRepository;
    private final PlacementHistoryService placementHistoryService;
    private final AuthService authService;

    @Transactional
    public ModulePlaceResponseDto placeModule(Long projectId, Long moduleId,
        ModulePlaceDto modulePlaceDto) {

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(project)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));

        MyModule myModule = myModuleRepository.findByProjectAndModule(project, module)
            .orElseThrow(() -> new CartNotFoundException("해당 항목을 찾을 수 없습니다."));


        if (myModule.getQuantity() <= myModule.getUsedQuantity()) {
            throw new IllegalArgumentException("마이 모듈에 있는 수량을 모두 사용하였습니다. 마이 모듈의 수량을 변경해주세요.");
        }

        myModule.incrementUsedQuantity();
        myModuleRepository.save(myModule);

        ProjectModule projectModule = ProjectModule.createMyModule(myModule, project,
            modulePlaceDto.getPositionX(), modulePlaceDto.getPositionY(),
            modulePlaceDto.getAngle());

        projectModuleRepository.save(projectModule);

        project.updatePlacedTotalPrice();
        projectRepository.save(project);

        placementHistoryService.saveHistory(project, module, projectModule, "ADD",
            modulePlaceDto.getPositionX(), modulePlaceDto.getPositionY(), modulePlaceDto.getAngle());

        String topImageUrl = module.getTopImage() != null ? module.getTopImage().getImageUrl() : null;

        return new ModulePlaceResponseDto(myModule.getId(), projectModule.getId(),
            myModule.getQuantity(), myModule.getUsedQuantity(), topImageUrl, modulePlaceDto.getPositionX(),
            modulePlaceDto.getPositionY(), modulePlaceDto.getAngle());
    }

    @Transactional
    public ModulePlaceUpdateResponseDto updatePlaceModule(Long projectId, Long projectModuleId,
        ModulePlaceDto modulePlaceDto) {

        ProjectModule projectModule = projectModuleRepository.findById(projectModuleId).orElseThrow(() -> new ProjectModuleNotFoundException("배치된 모듈을 찾을 수 없습니다."));
        Long moduleId = projectModule.getModule().getId();

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(projectModule.getProject())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        placementHistoryService.saveHistory(projectModule.getProject(), projectModule.getModule(), projectModule, "UPDATE",
            projectModule.getPositionX(), projectModule.getPositionY(), projectModule.getAngle());

        projectModule.updatePosition(modulePlaceDto.getPositionX(), modulePlaceDto.getPositionY(),
            modulePlaceDto.getAngle());

        projectModuleRepository.save(projectModule);

        return new ModulePlaceUpdateResponseDto(projectId, moduleId, projectModule.getPositionX(),
            projectModule.getPositionY(), projectModule.getAngle());
    }

    @Transactional
    public void deletePlaceModule(Long projectId, Long projectModuleId) {

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(project)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        ProjectModule projectModule = projectModuleRepository.findById(projectModuleId).orElseThrow(() -> new ProjectModuleNotFoundException("배치된 모듈을 찾을 수 없습니다."));

        if (projectModule.isDeleted())
            return;

        Module module = projectModule.getModule();

        MyModule myModule = myModuleRepository.findByProjectAndModule(project, module)
            .orElseThrow(() -> new CartNotFoundException("해당 항목을 찾을 수 없습니다."));

        placementHistoryService.saveHistory(project, module, projectModule, "DELETE",
            projectModule.getPositionX(), projectModule.getPositionY(), projectModule.getAngle());

        myModule.decrementUsedQuantity();
        myModuleRepository.save(myModule);

        projectModule.markAsDeleted(true);
        projectModuleRepository.save(projectModule);
    }

    @Transactional(readOnly = true)
    public ModulePositionListDto getModulePositions(Long projectId) {

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(project)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        List<ModulePositionDto> modulePositions = project.getProjectModules().stream()
            .filter(projectModule -> !projectModule.isDeleted())
            .map(projectModule -> new ModulePositionDto(
                projectModule.getId(),
                projectModule.getModule().getTopImage() != null ? projectModule.getModule().getTopImage().getImageUrl() : null, projectModule.getPositionX(),
                projectModule.getPositionY(), projectModule.getAngle()))
            .collect(Collectors.toList());

        return new ModulePositionListDto(modulePositions, project.getCanvas());
    }

    public CanvasResponseDto updateCanvas(Long projectId, CanvasDto canvasDto) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(project)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        project.setCanvas(canvasDto.getCanvasJson());
        projectRepository.save(project);
        return new CanvasResponseDto(canvasDto.getCanvasJson());
    }
}
