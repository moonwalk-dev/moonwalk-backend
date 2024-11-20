package kr.moonwalk.moonwalk_api.dto.project;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyModuleAddDto {

    private Long moduleId;

    @Min(value = 0, message = "최소 수량은 0개입니다.")
    private int quantity;
}
