package kr.moonwalk.moonwalk_api.dto.module;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class CategoryModulesResponseDto {

    private String categoryName;

    private List<CategoryModuleDto> modules;


}
