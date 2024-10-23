package kr.moonwalk.moonwalk_api.dto.space;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpaceDto {

    private String name;
    private String description;
    private String keyword;
    private String imagePath;
}
