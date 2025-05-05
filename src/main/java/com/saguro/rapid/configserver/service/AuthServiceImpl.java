package com.saguro.rapid.configserver.service;

import com.saguro.rapid.configserver.dto.LoginResponse;
import com.saguro.rapid.configserver.dto.UserInfo;
import com.saguro.rapid.configserver.entity.User;
import com.saguro.rapid.configserver.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
    JwtService jwtTokenProvider,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponse authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtTokenProvider.generateToken(authentication);
        
        UserInfo userInfo = new UserInfo(
            user.getId(),
            user.getUsername(),
            user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet())
        );

        return new LoginResponse(token, userInfo);
    }
}
