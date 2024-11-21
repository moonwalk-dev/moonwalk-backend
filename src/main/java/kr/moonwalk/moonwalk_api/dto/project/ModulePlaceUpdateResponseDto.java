package kr.moonwalk.moonwalk_api.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModulePlaceUpdateResponseDto {

    private Long projectId;

    private Long moduleId;

    private int positionX;

    private int positionY;

    private int angle;
}
