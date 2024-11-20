package kr.moonwalk.moonwalk_api.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModulePlaceResponseDto {

    private Long projectId;

    private Long moduleId;

    private Long myModuleId;

    private int quantity;

    private int usedQuantity;

    private int positionX;

    private int positionY;

    private int angle;
}
