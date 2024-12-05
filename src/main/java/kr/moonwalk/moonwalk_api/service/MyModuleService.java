package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import kr.moonwalk.moonwalk_api.domain.Module;
import kr.moonwalk.moonwalk_api.domain.MyModule;
import kr.moonwalk.moonwalk_api.domain.Project;
import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleAddDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleAddResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleDetailResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleListResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleSearchResultDto;
import kr.moonwalk.moonwalk_api.exception.notfound.CartNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ModuleNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ProjectNotFoundException;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
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
public class MyModuleService {

    private final ProjectRepository projectRepository;
    private final MyModuleRepository myModuleRepository;
    private final ModuleRepository moduleRepository;
    private final ProjectModuleRepository projectModuleRepository;
    private final PlacementHistoryService placementHistoryService;
    private final CategoryRepository categoryRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public MyModuleListResponseDto getFilteredMyModules(Long projectId,
        List<String> categoryNames) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(project)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        List<Long> categoryIds = categoryNames != null && !categoryNames.isEmpty()
            ? categoryRepository.findIdsByNameInAndType(categoryNames, Type.TYPE_MODULE) : null;

        List<MyModuleResponseDto> myModules = project.getMyModules().stream().filter(
            cart -> categoryIds == null || categoryIds.contains(
                cart.getModule().getCategory().getId())).map(
            myModule -> new MyModuleResponseDto(myModule.getId(), myModule.getProject().getId(),
                myModule.getModule().getId(), myModule.getModule().getName(),
                myModule.getModule().getCapacity(), myModule.getModule().getSerialNumber(),
                myModule.getModule().getIsoImage() != null ? myModule.getModule().getIsoImage()
                    .getImageUrl() : null, myModule.getQuantity())).collect(Collectors.toList());

        return new MyModuleListResponseDto(myModules);
    }

    @Transactional(readOnly = true)
    public MyModuleSearchResultDto searchMyModulesByName(Long projectId, String query) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(project)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        List<MyModule> myModules = myModuleRepository.findByProjectAndModuleNameContainingIgnoreCase(
            project, query);

        List<MyModuleResponseDto> myModuleDtos = myModules.stream().map(
            myModule -> new MyModuleResponseDto(myModule.getId(), project.getId(),
                myModule.getModule().getId(), myModule.getModule().getName(),
                myModule.getModule().getCapacity(), myModule.getModule().getSerialNumber(),
                myModule.getModule().getIsoImage() != null ? myModule.getModule().getIsoImage()
                    .getImageUrl() : null, myModule.getQuantity())).collect(Collectors.toList());

        return new MyModuleSearchResultDto(project.getId(), query, myModuleDtos);
    }

    @Transactional
    public MyModuleAddResponseDto addModule(Long projectId, MyModuleAddDto myModuleAddDto) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(project)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        Module module = moduleRepository.findById(myModuleAddDto.getModuleId())
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));

        MyModule myModule = myModuleRepository.findByProjectAndModule(project, module)
            .orElseGet(() -> new MyModule(project, module, myModuleAddDto.getQuantity()));

        if (myModule.getId() != null) {
            if (myModule.getUsedQuantity() > myModuleAddDto.getQuantity()) {
                throw new IllegalArgumentException(
                    "변경하려는 모듈 수량이 이미 사용된 수량보다 적습니다. (이미 사용된 수량: " + myModule.getUsedQuantity()
                        + ", 입력된 수량: " + myModuleAddDto.getQuantity() + ")");
            }
            myModule.setQuantity(myModuleAddDto.getQuantity());
        }
        myModuleRepository.save(myModule);

        project.updateEstimatedTotalPrice();
        projectRepository.save(project);

        return new MyModuleAddResponseDto(module.getId(), module.getName(), project.getId(),
            myModule.getQuantity(), myModule.getUsedQuantity());
    }

    @Transactional
    public void deleteMyModule(Long projectId, Long myModuleId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(project)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        MyModule myModule = myModuleRepository.findById(myModuleId)
            .orElseThrow(() -> new CartNotFoundException("해당 항목을 찾을 수 없습니다."));

        placementHistoryService.deleteByProjectAndModule(project, myModule.getModule());
        projectModuleRepository.deleteByProjectAndModule(project, myModule.getModule());

        project.getMyModules().remove(myModule);
        myModuleRepository.delete(myModule);
    }

    @Transactional(readOnly = true)
    public MyModuleDetailResponseDto getInfoMyModule(Long projectId, Long myModuleId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(project)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        MyModule myModule = myModuleRepository.findById(myModuleId)
            .orElseThrow(() -> new CartNotFoundException("해당 항목을 찾을 수 없습니다."));

        Module module = myModule.getModule();

        Category subCategory = module.getCategory();
        Category mainCategory = subCategory.getParentCategory();
        String size = module.getWidth() + "*" + module.getHeight();

        return new MyModuleDetailResponseDto(myModuleId, projectId, module.getId(),
            mainCategory != null ? mainCategory.getName() : null, subCategory.getName(),
            module.getName(), module.getCapacity(), module.getSerialNumber(),
            module.getIsoImage() != null ? module.getIsoImage().getImageUrl() : null, size,
            module.getMaterials(), myModule.getQuantity(), module.getPrice());
    }

}
