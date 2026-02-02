package dev.marcosoliveira.discography.application.service;

import dev.marcosoliveira.discography.api.dto.AuthRequestDTO;
import dev.marcosoliveira.discography.api.dto.AuthResponseDTO;
import dev.marcosoliveira.discography.api.dto.RefreshRequestDTO;
import dev.marcosoliveira.discography.domain.model.Role;
import dev.marcosoliveira.discography.domain.model.User;
import dev.marcosoliveira.discography.domain.repository.UserRepository;
import dev.marcosoliveira.discography.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO login(AuthRequestDTO request) {
        
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + request.getUsername()));

        return generateAuthResponseDTO(user);
    }

    @Transactional
    public AuthResponseDTO register(AuthRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();

        userRepository.save(user);

        return generateAuthResponseDTO(user);
    }

    public AuthResponseDTO refresh(RefreshRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Refresh token invalid or expired");
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));

        String newAccessToken = jwtService.generateAccessToken(user);

        return new AuthResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            newAccessToken,
            refreshToken,
            "Bearer",
            300L 
        );
    }

    private AuthResponseDTO generateAuthResponseDTO(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            accessToken,
            refreshToken,
            "Bearer",
            300L 
        );
    }
}
