package kr.moonwalk.moonwalk_api.service.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import kr.moonwalk.moonwalk_api.domain.RefreshToken;
import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.domain.User.Role;
import kr.moonwalk.moonwalk_api.dto.auth.JwtResponse;
import kr.moonwalk.moonwalk_api.dto.auth.LoginResponse;
import kr.moonwalk.moonwalk_api.dto.auth.UserLoginDto;
import kr.moonwalk.moonwalk_api.dto.auth.UserRegistrationDto;
import kr.moonwalk.moonwalk_api.dto.auth.UserResponseDto;
import kr.moonwalk.moonwalk_api.exception.auth.InvalidRefreshTokenException;
import kr.moonwalk.moonwalk_api.repository.RefreshTokenRepository;
import kr.moonwalk.moonwalk_api.repository.UserRepository;
import kr.moonwalk.moonwalk_api.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalStateException("이미 존재하는 이름입니다.");
        }

        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());
        User user = new User(registrationDto.getUsername(), registrationDto.getEmail(), encodedPassword, Role.ROLE_USER);
        User savedUser = userRepository.save(user);

        return new UserResponseDto(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
    }

    @Transactional
    public LoginResponse loginUser(UserLoginDto userLoginDto, HttpServletResponse response) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userLoginDto.getEmail(),
                userLoginDto.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(
            userLoginDto.getEmail());

        final String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        User user = userRepository.findByEmail(userLoginDto.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        setRefreshTokenCookie(response, refreshToken);

        RefreshToken refreshTokenEntity = new RefreshToken(refreshToken, userDetails.getUsername(),
            LocalDateTime.now().plusDays(30));
        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponse(user.getUsername(), user.getRole(), accessToken);
    }

    @Transactional
    public JwtResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = getRefreshTokenFromCookie(request);
            if (refreshToken == null) {
                throw new InvalidRefreshTokenException("리프레시 토큰이 없습니다.");
            }

            RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("리프레시 토큰이 유효하지 않습니다."));

            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                throw new InvalidRefreshTokenException("리프레시 토큰이 만료되었습니다.");
            }

            String email = storedToken.getEmail();
            String newAccessToken = jwtUtil.generateAccessToken(email);
            String newRefreshToken = refreshToken;

            if (jwtUtil.isRefreshTokenExpiringSoon(refreshToken)) {
                newRefreshToken = jwtUtil.generateRefreshToken(email);

                refreshTokenRepository.delete(storedToken);
                RefreshToken newToken = new RefreshToken(newRefreshToken, email, LocalDateTime.now().plusDays(30));
                refreshTokenRepository.save(newToken);

                setRefreshTokenCookie(response, newRefreshToken);
            }

            return new JwtResponse(newAccessToken);
        } catch (InvalidRefreshTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("리프레시 토큰 처리 중 오류 발생", e);
        }
    }



    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }
        clearRefreshTokenCookie(response);
    }


    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);    // 배포 시 true로 설정
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public User getCurrentAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        String userEmail = userDetails.getUsername();

        return userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserResponseDto getInfo() {
        User user = getCurrentAuthenticatedUser();
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}
