package kr.moonwalk.moonwalk_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDto {

    @Schema(description = "유저 id")
    private Long id;

}
