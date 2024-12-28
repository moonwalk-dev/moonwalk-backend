package kr.moonwalk.moonwalk_api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    @Schema(description = "유저 id")
    private Long id;

    private String username;

    private String email;

}
