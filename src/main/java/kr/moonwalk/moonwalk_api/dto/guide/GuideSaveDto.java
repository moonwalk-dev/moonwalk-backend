package kr.moonwalk.moonwalk_api.dto.guide;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GuideSaveDto {

    private String name;

    private String description;

    private List<String> keywords;

    private Long categoryId;
}
