package kr.moonwalk.moonwalk_api.dto.auth;

import kr.moonwalk.moonwalk_api.domain.User.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String username;

    private Role role;

    private String accessToken;
}
