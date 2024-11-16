package kr.moonwalk.moonwalk_api.dto.mood;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EstimateMoodResponseDto {

    private Long id;

    private Long moodId;

    private String moodName;

    private String coverImage;

}
