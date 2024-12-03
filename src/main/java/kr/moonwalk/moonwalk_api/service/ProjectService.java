package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Estimate;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.domain.Project;
import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.dto.mypage.ProjectInfoDto;
import kr.moonwalk.moonwalk_api.dto.mypage.ProjectInfoListDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectCreateDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectCreateResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectPriceResponseDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectSaveDto;
import kr.moonwalk.moonwalk_api.dto.project.ProjectSaveResponseDto;
import kr.moonwalk.moonwalk_api.exception.notfound.EstimateNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ProjectNotFoundException;
import kr.moonwalk.moonwalk_api.repository.EstimateRepository;
import kr.moonwalk.moonwalk_api.repository.ProjectModuleRepository;
import kr.moonwalk.moonwalk_api.repository.ProjectRepository;
import kr.moonwalk.moonwalk_api.service.auth.AuthService;
import kr.moonwalk.moonwalk_api.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EstimateRepository estimateRepository;
    private final ImageService imageService;
    private final ProjectModuleRepository projectModuleRepository;
    private final PlacementHistoryService placementHistoryService;
    private final AuthService authService;

    @Transactional
    public ProjectCreateResponseDto createProject(ProjectCreateDto projectCreateDto,
        MultipartFile coverImageFile,
        MultipartFile blueprintImageFile) {

        User user = authService.getCurrentAuthenticatedUser();
        Project project;

        if (projectCreateDto.getEstimateId() == null) {
            project = new Project(user, projectCreateDto.getTitle(), projectCreateDto.getClient(),
                projectCreateDto.getArea());
        } else {
            Estimate estimate = estimateRepository.findById(projectCreateDto.getEstimateId())
                .orElseThrow(() -> new EstimateNotFoundException("해당 견적을 찾을 수 없습니다."));
            project = new Project(estimate, user);
        }

        if (blueprintImageFile != null) {
            String blueprintExtension = FileUtil.getFileExtension(blueprintImageFile.getOriginalFilename());
            String blueprintImagePath =
                user.getEmail() + "/projects/" + project.getTitle() + "/blueprint."
                    + blueprintExtension;
            Image blueprintImage = imageService.uploadAndSaveImage(blueprintImageFile,
                blueprintImagePath);
            project.setBlueprintImage(blueprintImage);
        }
        if (coverImageFile != null) {
            String coverExtension = FileUtil.getFileExtension(coverImageFile.getOriginalFilename());
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

    @Transactional(readOnly = true)
    public ProjectPriceResponseDto getProjectPriceDetails(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(project)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

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

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(project)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        project.save(projectSaveDto.getTitle(), projectSaveDto.getClient(),
            projectSaveDto.getArea());

        if (coverImageFile != null) {
            String coverExtension = FileUtil.getFileExtension(coverImageFile.getOriginalFilename());
            String coverImagePath =
                user.getEmail() + "/projects/" + project.getTitle() + "/cover."
                    + coverExtension;
            Image coverImage = imageService.uploadAndSaveImage(coverImageFile,
                coverImagePath);
            project.setCoverImage(coverImage);
        }

        placementHistoryService.clearHistory(projectId);

        Project savedProject = projectRepository.save(project);
        return new ProjectSaveResponseDto(savedProject.getId());
    }

    @Transactional
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();
        if (!user.getProjects().contains(project)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        user.getProjects().remove(project);

        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public ProjectInfoListDto getProjects() {
        User user = authService.getCurrentAuthenticatedUser();

        List<ProjectInfoDto> projects = user.getProjects().stream().map(
            project -> new ProjectInfoDto(project.getId(), project.getTitle(), project.getClient(),
                project.getArea(), project.getEstimatedTotalPrice(), project.getPlacedTotalPrice(),
                project.getCreatedAt(),
                project.getCoverImage() != null ? project.getCoverImage().getImageUrl()
                    : null)).collect(Collectors.toList());

        return new ProjectInfoListDto(projects);
    }

}
