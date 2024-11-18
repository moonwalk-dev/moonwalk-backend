package kr.moonwalk.moonwalk_api.service;

import kr.moonwalk.moonwalk_api.domain.Estimate;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.domain.Project;
import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.dto.project.ProjectCreateResponseDto;
import kr.moonwalk.moonwalk_api.exception.notfound.EstimateNotFoundException;
import kr.moonwalk.moonwalk_api.repository.EstimateRepository;
import kr.moonwalk.moonwalk_api.repository.ProjectRepository;
import kr.moonwalk.moonwalk_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EstimateRepository estimateRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;

    public ProjectCreateResponseDto createProject(Long estimateId, MultipartFile blueprintImageFile) {

        User user = getCurrentAuthenticatedUser();

        Estimate estimate = estimateRepository.findById(estimateId)
            .orElseThrow(() -> new EstimateNotFoundException("견적을 찾을 수 없습니다."));

        Project project = new Project(estimate, user);

        if (blueprintImageFile != null) {
            String blueprintExtension = getFileExtension(blueprintImageFile.getOriginalFilename());
            String blueprintImagePath = user.getEmail() + "/projects/" + project.getTitle() + "/blueprint." + blueprintExtension;
            Image blueprintImage = imageService.uploadAndSaveImage(blueprintImageFile, blueprintImagePath);
            project.setBlueprintImage(blueprintImage);
        }
        Project savedProject = projectRepository.save(project);

        return new ProjectCreateResponseDto(savedProject.getId());
    }

    private User getCurrentAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
}
