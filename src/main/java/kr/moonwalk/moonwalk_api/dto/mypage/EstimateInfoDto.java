package kr.moonwalk.moonwalk_api.dto.mypage;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EstimateInfoDto {

    private Long id;

    private String title;

    private int totalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    private Long moodId;
}
