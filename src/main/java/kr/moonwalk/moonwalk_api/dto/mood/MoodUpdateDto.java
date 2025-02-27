package kr.moonwalk.moonwalk_api.dto.mood;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoodUpdateDto {
    private MoodSaveDto mood;
    private List<Long> deleteImageIds;
}
