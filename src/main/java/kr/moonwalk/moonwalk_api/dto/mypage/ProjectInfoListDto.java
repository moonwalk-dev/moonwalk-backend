package kr.moonwalk.moonwalk_api.dto.mypage;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectInfoListDto {

    private List<ProjectInfoDto> projects;
}
