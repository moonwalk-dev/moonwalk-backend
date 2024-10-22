package kr.moonwalk.moonwalk_api.dto.auth;

import lombok.Data;

@Data
public class UserLoginDto {

    private String username;
    private String password;

}
