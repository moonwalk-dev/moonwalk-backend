package kr.moonwalk.moonwalk_api.dto.project;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyModuleSearchResultDto {

    private Long projectId;

    private String searchQuery;

    private List<MyModuleResponseDto> myModules;

}
