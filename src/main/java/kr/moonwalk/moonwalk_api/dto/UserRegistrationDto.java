package kr.moonwalk.moonwalk_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @Schema(description = "아이디")
    private String username;

    @Schema(description = "비밀번호")
    private String password;

    @Schema(description = "실명")
    private String realname;

    @Schema(description = "연락처")
    private String phoneNumber;

}
