package kr.moonwalk.moonwalk_api.dto.mood;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoodListResponseDto {

    private List<MoodDto> moods;
}
