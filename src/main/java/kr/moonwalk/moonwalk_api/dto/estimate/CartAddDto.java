package kr.moonwalk.moonwalk_api.dto.estimate;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartAddDto {

    private Long moduleId;

    @Min(value = 1, message = "최소 수량은 1개입니다.")
    private int quantity;

}
