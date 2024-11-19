package kr.moonwalk.moonwalk_api.service.auth;

import java.time.LocalDateTime;
import kr.moonwalk.moonwalk_api.domain.RefreshToken;
import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.domain.User.Role;
import kr.moonwalk.moonwalk_api.dto.auth.JwtResponse;
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
import org.springframework.security.core.userdetails.UserDetails;
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

        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());
        User user = new User(registrationDto.getEmail(), encodedPassword, Role.ROLE_USER);
        User savedUser = userRepository.save(user);

        return new UserResponseDto(savedUser.getId());
    }

    @Transactional
    public JwtResponse loginUser(UserLoginDto userLoginDto) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userLoginDto.getEmail(),
                userLoginDto.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(
            userLoginDto.getEmail());

        final String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        RefreshToken refreshTokenEntity = new RefreshToken(refreshToken, userDetails.getUsername(),
            LocalDateTime.now().plusDays(30));
        refreshTokenRepository.save(refreshTokenEntity);

        return new JwtResponse(accessToken, refreshToken);
    }

    @Transactional
    public JwtResponse refreshAccessToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken).orElseThrow(
            () -> new InvalidRefreshTokenException("Refresh token is invalid or expired"));

        if (storedToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Refresh token is expired");
        }

        String email = storedToken.getEmail();
        String newAccessToken = jwtUtil.generateAccessToken(email);

        String newRefreshToken = refreshToken;
        if (jwtUtil.isRefreshTokenExpiringSoon(refreshToken)) {
            newRefreshToken = jwtUtil.generateRefreshToken(email);

            storedToken = new RefreshToken(newRefreshToken, email,
                LocalDateTime.now().plusDays(30));
            refreshTokenRepository.save(storedToken);
        }

        return new JwtResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}
