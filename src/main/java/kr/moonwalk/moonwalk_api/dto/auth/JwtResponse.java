package kr.moonwalk.moonwalk_api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponse {

    private String accessToken;
}
