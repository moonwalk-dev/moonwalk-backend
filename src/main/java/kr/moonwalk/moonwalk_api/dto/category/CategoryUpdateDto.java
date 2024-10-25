package kr.moonwalk.moonwalk_api.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryUpdateDto {

    @NotBlank(message = "카테고리 명은 필수입니다.")
    private String name;
}
