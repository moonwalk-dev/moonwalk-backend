package kr.moonwalk.moonwalk_api.dto.guide;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuideSaveResponseDto {

    private Long id;

    private String name;

    private String description;

    private List<String> keywords;

}
