package kr.moonwalk.moonwalk_api.dto.mood;

import java.util.List;
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

    @Getter
    @AllArgsConstructor
    public static class ImageDto {
        private Long id;
        private String url;
    }
}