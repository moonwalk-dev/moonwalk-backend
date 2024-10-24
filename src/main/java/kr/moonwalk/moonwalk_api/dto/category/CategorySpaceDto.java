package kr.moonwalk.moonwalk_api.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategorySpaceDto {

    private Long id;
    private String name;
    private String coverImage;
}
