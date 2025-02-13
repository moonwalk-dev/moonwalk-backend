package kr.moonwalk.moonwalk_api.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectCreateDto {

    private Long estimateId;
    private String title;
    private String client;
    private String area;
}
