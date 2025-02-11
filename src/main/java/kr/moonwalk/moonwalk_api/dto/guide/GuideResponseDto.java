package kr.moonwalk.moonwalk_api.dto.guide;

import java.util.List;
import kr.moonwalk.moonwalk_api.dto.ImageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuideResponseDto {
    private Long id;
    private String name;
    private String description;
    private List<String> keywords;
    private ImageDto coverImage;
    private List<ImageDto> detailImages;

}