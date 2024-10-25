package kr.moonwalk.moonwalk_api.dto.category;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryListResponseDto {
    private List<CategoryResponseDto> categories;
}