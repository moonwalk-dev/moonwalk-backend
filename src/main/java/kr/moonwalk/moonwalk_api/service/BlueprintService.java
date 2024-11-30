package kr.moonwalk.moonwalk_api.service;

import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.domain.Project;
import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.dto.project.ProjectBlueprintResponseDto;
import kr.moonwalk.moonwalk_api.exception.notfound.ProjectNotFoundException;
import kr.moonwalk.moonwalk_api.repository.ProjectRepository;
import kr.moonwalk.moonwalk_api.repository.UserRepository;
import kr.moonwalk.moonwalk_api.service.auth.AuthService;
import kr.moonwalk.moonwalk_api.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BlueprintService {

    private final ProjectRepository projectRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional
    public ProjectBlueprintResponseDto addOrUpdateBlueprint(Long projectId, MultipartFile blueprintImageFile) {

        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        User user = authService.getCurrentAuthenticatedUser();

        String blueprintExtension = FileUtil.getFileExtension(blueprintImageFile.getOriginalFilename());
        String blueprintImagePath =
            user.getEmail() + "/projects/" + project.getTitle() + "/blueprint." + blueprintExtension;

        Image blueprintImage = imageService.updateImage(blueprintImageFile, blueprintImagePath, project.getBlueprintImage());
        project.setBlueprintImage(blueprintImage);

        projectRepository.save(project);

        return new ProjectBlueprintResponseDto(projectId, project.getBlueprintImage().getImageUrl());
    }

    @Transactional
    public void deleteBlueprint(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        if (project.getBlueprintImage() != null) {

            Image image = project.getBlueprintImage();
            imageService.deleteImage(image);

            project.setBlueprintImage(null);
            projectRepository.save(project);
        }
    }

    @Transactional(readOnly = true)
    public ProjectBlueprintResponseDto getBlueprintUrl(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("프로젝트를 찾을 수 없습니다."));

        Image image = project.getBlueprintImage();
        if (image == null)
            throw new ProjectNotFoundException("도면 이미지가 존재하지 않습니다.");

        return new ProjectBlueprintResponseDto(projectId, image.getImageUrl());
    }
}
