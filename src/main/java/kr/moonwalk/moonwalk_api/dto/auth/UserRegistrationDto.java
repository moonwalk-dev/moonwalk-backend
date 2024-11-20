package kr.moonwalk.moonwalk_api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "이름 필수입니다.")
    @Schema(description = "이름", defaultValue = "문워크디자인")
    private String username;

    @NotBlank(message = "이메일 필수입니다.")
    @Schema(description = "이메일", defaultValue = "moonwalk@gmail.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Schema(description = "비밀번호", defaultValue = "moonwalk123")
    private String password;

}
