package kr.moonwalk.moonwalk_api.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModulePositionDto {

    private String moduleImageUrl;

    private int positionX;

    private int positionY;

    private int angle;
}
