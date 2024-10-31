package kr.moonwalk.moonwalk_api.service.auth;

import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.domain.User.Role;
import kr.moonwalk.moonwalk_api.dto.auth.JwtResponse;
import kr.moonwalk.moonwalk_api.dto.auth.UserLoginDto;
import kr.moonwalk.moonwalk_api.dto.auth.UserRegistrationDto;
import kr.moonwalk.moonwalk_api.dto.auth.UserResponseDto;
import kr.moonwalk.moonwalk_api.exception.auth.InvalidRefreshTokenException;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;


    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {

        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalStateException("이미 존재하는 사용자명입니다.");
        }

        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());
        User user = new User(registrationDto.getUsername(), encodedPassword, Role.ROLE_USER);
        User savedUser = userRepository.save(user);

        return new UserResponseDto(savedUser.getId());
    }

    public JwtResponse loginUser(UserLoginDto userLoginDto) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(),
                userLoginDto.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(
            userLoginDto.getUsername());

        final String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());


        return new JwtResponse(accessToken, refreshToken);
    }

    public JwtResponse refreshAccessToken(String refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException("Refresh token is invalid or expired");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);

        String newRefreshToken = refreshToken;
        if (jwtUtil.isRefreshTokenExpiringSoon(refreshToken)) {
            newRefreshToken = jwtUtil.generateRefreshToken(username);
        }

        return new JwtResponse(newAccessToken, newRefreshToken);
    }


}
