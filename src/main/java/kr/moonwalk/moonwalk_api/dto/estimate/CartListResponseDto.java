package kr.moonwalk.moonwalk_api.dto.estimate;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartListResponseDto {

    private List<CartResponseDto> carts;
}
