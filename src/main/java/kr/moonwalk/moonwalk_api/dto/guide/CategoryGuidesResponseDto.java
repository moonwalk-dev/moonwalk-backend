package kr.moonwalk.moonwalk_api.dto.guide;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryGuidesResponseDto {

    private String categoryName;

    private List<CategoryGuideDto> guides;

}
