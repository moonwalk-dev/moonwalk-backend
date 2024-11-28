package kr.moonwalk.moonwalk_api.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModulePlaceResponseDto {

    private Long myModuleId;

    private Long projectModuleId;

    private int quantity;

    private int usedQuantity;

    private String moduleImageUrl;

    private int positionX;

    private int positionY;

    private int angle;
}
