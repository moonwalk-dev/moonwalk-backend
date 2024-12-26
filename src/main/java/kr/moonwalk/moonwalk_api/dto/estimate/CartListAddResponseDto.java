package kr.moonwalk.moonwalk_api.dto.estimate;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartListAddResponseDto {
    private Long estimateId;
    private List<CartAddResponseDto> addedCarts;
}