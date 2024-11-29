package kr.moonwalk.moonwalk_api.dto.guide;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuideSaveDto {

    private String name;

    private String description;

    private List<String> keywords;

    private Long categoryId;
}
