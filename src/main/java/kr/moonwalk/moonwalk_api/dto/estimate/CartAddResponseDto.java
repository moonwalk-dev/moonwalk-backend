package kr.moonwalk.moonwalk_api.dto.estimate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartAddResponseDto {

    private Long moduleId;

    private String moduleName;

    private Long estimateId;

    private int quantity;
}
