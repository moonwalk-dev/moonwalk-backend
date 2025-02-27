package kr.moonwalk.moonwalk_api.dto.mood;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoodDto {

    private Long id;

    private String name;

    private String coverImage;
}
