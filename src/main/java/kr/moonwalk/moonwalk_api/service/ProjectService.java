package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Estimate;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.domain.Module;
import kr.moonwalk.moonwalk_api.domain.MyModule;
import kr.moonwalk.moonwalk_api.domain.Project;
import kr.moonwalk.moonwalk_api.domain.ProjectModule;
import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ModulePlaceUpdateResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleAddDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleAddResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleDetailResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleListResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.MyModuleSearchResultDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectCreateDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectCreateResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectPriceResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectSaveDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectSaveResponseDto;
import kr.moonwalk.moonwalk_api.exception.notfound.CartNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.EstimateNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ModuleNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ProjectModuleNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ProjectNotFoundException;
import kr.moonwalk.moonwalk_api.repository.EstimateRepository;
import kr.moonwalk.moonwalk_api.repository.ModuleRepository;
import kr.moonwalk.moonwalk_api.repository.MyModuleRepository;
import kr.moonwalk.moonwalk_api.repository.ProjectModuleRepository;
import kr.moonwalk.moonwalk_api.repository.ProjectRepository;
import kr.moonwalk.moonwalk_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EstimateRepository estimateRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final MyModuleRepository myModuleRepository;
    private final ModuleRepository moduleRepository;
    private final ProjectModuleRepository projectModuleRepository;

    @Transactional
    public ProjectCreateResponseDto createProject(ProjectCreateDto projectCreateDto, MultipartFile coverImageFile,
        MultipartFile blueprintImageFile) {

        User user = getCurrentAuthenticatedUser();
        Project project;

        if (projectCreateDto.getEstimateId() == null) {
            project = new Project(user, projectCreateDto.getTitle(), projectCreateDto.getClient(), projectCreateDto.getArea());
        } else {
            Estimate estimate = estimateRepository.findById(projectCreateDto.getEstimateId())
                .orElseThrow(() -> new EstimateNotFoundException("해당 견적을 찾을 수 없습니다."));
            project = new Project(estimate, user);
        }

        if (blueprintImageFile != null) {
            String blueprintExtension = getFileExtension(blueprintImageFile.getOriginalFilename());
            String blueprintImagePath =
                user.getEmail() + "/projects/" + project.getTitle() + "/blueprint."
                    + blueprintExtension;
            Image blueprintImage = imageService.uploadAndSaveImage(blueprintImageFile,
                blueprintImagePath);
            project.setBlueprintImage(blueprintImage);
        }
        if (coverImageFile != null) {
            String coverExtension = getFileExtension(coverImageFile.getOriginalFilename());
            String coverImagePath =
                user.getEmail() + "/projects/" + project.getTitle() + "/cover."
                    + coverExtension;
            Image coverImage = imageService.uploadAndSaveImage(coverImageFile,
                coverImagePath);
            project.setCoverImage(coverImage);
        }
        Project savedProject = projectRepository.save(project);

        return new ProjectCreateResponseDto(savedProject.getId());
    }

    private User getCurrentAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        String userEmail = userDetails.getUsername();

        return userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }

    @Transactional(readOnly = true)
    public MyModuleListResponseDto getFilteredMyModules(Long projectId, List<Long> categoryIds) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        List<MyModuleResponseDto> myModules = project.getMyModules().stream().filter(
                myModule -> categoryIds == null || categoryIds.isEmpty() || categoryIds.contains(
                    myModule.getModule().getCategory().getId())).map(
                myModule -> new MyModuleResponseDto(myModule.getId(), myModule.getProject().getId(),
                    myModule.getModule().getId(), myModule.getModule().getName(),
                    myModule.getModule().getCapacity(), myModule.getModule().getSerialNumber(),
                    myModule.getModule().getIsoImage().getImageUrl(), myModule.getQuantity()))
            .collect(Collectors.toList());

        return new MyModuleListResponseDto(myModules);
    }

    @Transactional(readOnly = true)
    public MyModuleSearchResultDto searchMyModulesByName(Long projectId, String query) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        List<MyModule> myModules = myModuleRepository.findByProjectAndModuleNameContainingIgnoreCase(
            project, query);

        List<MyModuleResponseDto> myModuleDtos = myModules.stream().map(
                myModule -> new MyModuleResponseDto(myModule.getId(), project.getId(),
                    myModule.getModule().getId(), myModule.getModule().getName(),
                    myModule.getModule().getCapacity(), myModule.getModule().getSerialNumber(),
                    myModule.getModule().getIsoImage() != null ? myModule.getModule().getIsoImage()
                        .getImageUrl() : "default-image-url", myModule.getQuantity()))
            .collect(Collectors.toList());

        return new MyModuleSearchResultDto(project.getId(), query, myModuleDtos);
    }

    @Transactional
    public MyModuleAddResponseDto addModule(Long projectId, MyModuleAddDto myModuleAddDto) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

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

        MyModule myModule = myModuleRepository.findById(myModuleId)
            .orElseThrow(() -> new CartNotFoundException("해당 항목을 찾을 수 없습니다."));

        project.getMyModules().remove(myModule);
        myModuleRepository.delete(myModule);
    }

    @Transactional
    public ModulePlaceResponseDto placeModule(Long projectId, Long moduleId,
        ModulePlaceDto modulePlaceDto) {

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));

        MyModule myModule = myModuleRepository.findByProjectAndModule(project, module)
            .orElseThrow(() -> new CartNotFoundException("해당 항목을 찾을 수 없습니다."));

        if (myModule.getQuantity() <= myModule.getUsedQuantity()) {
            throw new IllegalArgumentException("마이 모듈에 있는 수량을 모두 사용하였습니다. 마이 모듈의 수량을 변경해주세요.");
        }
        myModule.updateUsedQuantity();

        ProjectModule projectModule = ProjectModule.createMyModule(myModule, project,
            modulePlaceDto.getPositionX(), modulePlaceDto.getPositionY(),
            modulePlaceDto.getAngle());

        projectModuleRepository.save(projectModule);

        project.updatePlacedTotalPrice();
        projectRepository.save(project);

        return new ModulePlaceResponseDto(projectId, moduleId, myModule.getId(),
            myModule.getQuantity(), myModule.getUsedQuantity(), modulePlaceDto.getPositionX(),
            modulePlaceDto.getPositionY(), modulePlaceDto.getAngle());
    }

    @Transactional(readOnly = true)
    public MyModuleDetailResponseDto getInfoMyModule(Long projectId, Long myModuleId) {
        MyModule myModule = myModuleRepository.findById(myModuleId)
            .orElseThrow(() -> new CartNotFoundException("해당 항목을 찾을 수 없습니다."));

        Module module = myModule.getModule();

        Category subCategory = module.getCategory();
        Category mainCategory = subCategory.getParentCategory();

        return new MyModuleDetailResponseDto(myModuleId, projectId, module.getId(),
            mainCategory != null ? mainCategory.getName() : null, subCategory.getName(),
            module.getName(), module.getCapacity(), module.getSerialNumber(),
            module.getIsoImage().getImageUrl(), module.getSize(), module.getMaterial(),
            myModule.getQuantity(), module.getPrice());
    }

    @Transactional
    public ModulePlaceUpdateResponseDto updatePlaceModule(Long projectId, Long moduleId,
        ModulePlaceDto modulePlaceDto) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));

        ProjectModule projectModule = projectModuleRepository.findByProjectAndModule(project,
            module).orElseThrow(() -> new ProjectModuleNotFoundException("배치된 모듈을 찾을 수 없습니다."));

        projectModule.updatePosition(modulePlaceDto.getPositionX(), modulePlaceDto.getPositionY(),
            modulePlaceDto.getAngle());

        projectModuleRepository.save(projectModule);

        return new ModulePlaceUpdateResponseDto(projectId, moduleId, projectModule.getPositionX(),
            projectModule.getPositionY(), projectModule.getAngle());
    }

    @Transactional(readOnly = true)
    public ProjectPriceResponseDto getProjectPriceDetails(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        int placedTotalPrice = project.getPlacedTotalPrice();
        int estimatedTotalPrice = project.getEstimatedTotalPrice();

        List<Object[]> queryResults = projectModuleRepository.findCategoryPriceDetailsByProjectId(
            projectId);

        Map<String, List<Object[]>> groupedByCategory = queryResults.stream()
            .collect(Collectors.groupingBy(row -> (String) row[0]));

        List<ProjectPriceResponseDto.CategoryPriceResponseDto> categoryPriceList = groupedByCategory.entrySet()
            .stream().map(entry -> {
                String categoryName = entry.getKey();
                List<Object[]> modules = entry.getValue();

                List<ProjectPriceResponseDto.CategoryPriceResponseDto.ModuleDetail> moduleDetails = modules.stream()
                    .map(row -> new ProjectPriceResponseDto.CategoryPriceResponseDto.ModuleDetail(
                        (String) row[1], ((Number) row[2]).intValue())).toList();

                int categoryTotalPrice = modules.stream()
                    .mapToInt(row -> ((Number) row[2]).intValue()).sum();

                int moduleCount = modules.size();

                return new ProjectPriceResponseDto.CategoryPriceResponseDto(categoryName,
                    categoryTotalPrice, moduleCount, moduleDetails);
            }).toList();

        return new ProjectPriceResponseDto(placedTotalPrice, estimatedTotalPrice,
            categoryPriceList);
    }


    @Transactional
    public ProjectSaveResponseDto save(Long projectId, ProjectSaveDto projectSaveDto,
        MultipartFile coverImageFile) {

        User user = getCurrentAuthenticatedUser();

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        project.save(projectSaveDto.getTitle(), projectSaveDto.getClient(),
            projectSaveDto.getArea());

        if (coverImageFile != null) {
            String coverExtension = getFileExtension(coverImageFile.getOriginalFilename());
            String coverImagePath =
                user.getEmail() + "/projects/" + project.getTitle() + "/cover."
                    + coverExtension;
            Image coverImage = imageService.uploadAndSaveImage(coverImageFile,
                coverImagePath);
            project.setCoverImage(coverImage);
        }
        Project savedProject = projectRepository.save(project);
        return new ProjectSaveResponseDto(savedProject.getId());
    }
}
