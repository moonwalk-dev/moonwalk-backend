package kr.moonwalk.moonwalk_api.dto.guide;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuideUpdateDto {

    private GuideSaveDto guide;

    private List<Long> deleteImageIds;
}