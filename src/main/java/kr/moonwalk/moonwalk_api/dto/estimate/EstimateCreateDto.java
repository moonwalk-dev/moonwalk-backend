package kr.moonwalk.moonwalk_api.dto.estimate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EstimateCreateDto {
    private String title;
    private String client;
    private String area;
}
