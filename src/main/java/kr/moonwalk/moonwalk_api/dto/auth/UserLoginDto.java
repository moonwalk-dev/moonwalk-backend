package kr.moonwalk.moonwalk_api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {

    @NotBlank(message = "이메일 필수입니다.")
    @Schema(description = "이메일", defaultValue = "moonwalk@gmail.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(description = "비밀번호", defaultValue = "moonwalk123")
    private String password;

}
