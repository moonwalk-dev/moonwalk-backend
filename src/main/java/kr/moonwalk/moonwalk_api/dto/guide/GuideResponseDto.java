package kr.moonwalk.moonwalk_api.dto.guide;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuideResponseDto {

    private Long id;

    private String name;

    private String description;

    private List<String> keywords;

    private String coverImage;

    private List<String> detailImages;
}
