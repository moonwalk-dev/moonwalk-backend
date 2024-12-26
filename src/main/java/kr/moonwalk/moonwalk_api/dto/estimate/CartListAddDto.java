package kr.moonwalk.moonwalk_api.dto.estimate;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartListAddDto {
    @NotNull(message = "모듈과 수량 정보는 필수입니다.")
    private List<CartAddDto> modules;
}
