package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import kr.moonwalk.moonwalk_api.dto.auth.JwtResponse;
import kr.moonwalk.moonwalk_api.dto.auth.UserLoginDto;
import kr.moonwalk.moonwalk_api.dto.auth.UserRegistrationDto;
import kr.moonwalk.moonwalk_api.dto.auth.UserResponseDto;
import kr.moonwalk.moonwalk_api.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "이미 존재하는 사용자명")})
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserRegistrationDto userDto) {

        UserResponseDto response = authService.registerUser(userDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 사용자명 또는 비밀번호")})
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userLoginDto) {
        JwtResponse jwtResponse = authService.loginUser(userLoginDto);
        return ResponseEntity.ok(jwtResponse);
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<?> refreshToken(
        @RequestHeader("Authorization") String refreshTokenHeader) {

        String refreshToken = refreshTokenHeader.substring(7);

        JwtResponse jwtResponse = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(jwtResponse);
    }
}
