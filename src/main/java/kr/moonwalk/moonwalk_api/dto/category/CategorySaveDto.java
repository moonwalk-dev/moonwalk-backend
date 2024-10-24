package kr.moonwalk.moonwalk_api.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategorySaveDto {

    private String name;

    private Long parentId;

}
