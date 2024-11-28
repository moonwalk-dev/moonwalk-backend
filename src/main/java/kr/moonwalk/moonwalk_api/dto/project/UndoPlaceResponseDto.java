package kr.moonwalk.moonwalk_api.dto.project;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UndoPlaceResponseDto {
    private Long projectId;
    private Long myModuleId;
    private int usedQuantity;
    private List<ModulePositionDto> updatedPositions;
}
