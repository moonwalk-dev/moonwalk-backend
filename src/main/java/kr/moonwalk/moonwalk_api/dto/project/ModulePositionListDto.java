package kr.moonwalk.moonwalk_api.dto.project;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModulePositionListDto {

    private List<ModulePositionDto> modulePositions;
    private String canvasJson;
}
