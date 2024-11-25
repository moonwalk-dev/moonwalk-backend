package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.dto.mypage.EstimateInfoDto;
import kr.moonwalk.moonwalk_api.dto.mypage.EstimateInfoListDto;
import kr.moonwalk.moonwalk_api.dto.mypage.ProjectInfoDto;
import kr.moonwalk.moonwalk_api.dto.mypage.ProjectInfoListDto;
import kr.moonwalk.moonwalk_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;

    public ProjectInfoListDto getProjects() {
        User user = getCurrentAuthenticatedUser();

        List<ProjectInfoDto> projects = user.getProjects().stream().map(
            project -> new ProjectInfoDto(project.getId(), project.getTitle(), project.getClient(),
                project.getArea(), project.getEstimatedTotalPrice(), project.getPlacedTotalPrice(),
                project.getCreatedAt(),
                project.getCoverImage() != null ? project.getCoverImage().getImageUrl()
                    : null)).collect(Collectors.toList());

        return new ProjectInfoListDto(projects);
    }

    private User getCurrentAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        String userEmail = userDetails.getUsername();

        return userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public EstimateInfoListDto getEstimates() {

        User user = getCurrentAuthenticatedUser();

        List<EstimateInfoDto> estimates = user.getEstimates().stream().map(
                estimate -> new EstimateInfoDto(estimate.getId(), estimate.getTitle(),
                    estimate.getTotalPrice(), estimate.getCreatedAt(), estimate.getMood() != null ? estimate.getMood().getId()
                    : null))
            .collect(Collectors.toList());

        return new EstimateInfoListDto(estimates);
    }
}
