package kr.moonwalk.moonwalk_api.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectBlueprintResponseDto {

    private Long projectId;
    private String blueprintImageUrl;
}
