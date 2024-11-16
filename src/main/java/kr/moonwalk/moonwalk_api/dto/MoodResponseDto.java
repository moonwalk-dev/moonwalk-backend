package kr.moonwalk.moonwalk_api.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoodResponseDto {

    private Long id;

    private String name;

    private String description;

    private String coverImage;

    private List<String> detailImages;

}
