package kr.moonwalk.moonwalk_api.dto.estimate;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EstimateResponseDto {

    private Long id;

    private Long moodId;

    private String moodName;

    private String coverImage;

    private int totalPrice;

    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

}
