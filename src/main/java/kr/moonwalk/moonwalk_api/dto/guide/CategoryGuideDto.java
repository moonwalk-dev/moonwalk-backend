package kr.moonwalk.moonwalk_api.dto.guide;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryGuideDto {

    private Long id;

    private String name;

    private String coverImage;

}
