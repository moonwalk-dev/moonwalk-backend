package kr.moonwalk.moonwalk_api.dto.space;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoriesSpacesDto {

    private String categoryName;
    private List<SpaceDto> spaces;
}
