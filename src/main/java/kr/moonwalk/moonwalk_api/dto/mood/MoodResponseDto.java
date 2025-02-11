package kr.moonwalk.moonwalk_api.dto.mood;

import java.util.List;
import kr.moonwalk.moonwalk_api.dto.ImageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoodResponseDto {
    private Long id;
    private String name;
    private String description;
    private ImageDto coverImage;
    private List<ImageDto> detailImages;

}