package kr.moonwalk.moonwalk_api.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
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
}
