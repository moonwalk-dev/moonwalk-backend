package kr.moonwalk.moonwalk_api.dto.mypage;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ProjectInfoDto {
    private Long id;
    private String title;
    private String client;
    private String area;
    private int estimatedTotalPrice;
    private int placedTotalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    private String coverImageUrl;

    public ProjectInfoDto(Long id, String title, String client, String area,
        int estimatedTotalPrice, int placedTotalPrice,
        LocalDateTime createdAt, String coverImageUrl) {
        this.id = id;
        this.title = title;
        this.client = client;
        this.area = area;
        this.estimatedTotalPrice = estimatedTotalPrice;
        this.placedTotalPrice = placedTotalPrice;
        this.createdAt = createdAt;
        this.coverImageUrl = coverImageUrl;
    }
}