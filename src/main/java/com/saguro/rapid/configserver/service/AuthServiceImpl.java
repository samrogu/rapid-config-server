package com.saguro.rapid.configserver.service;

import com.saguro.rapid.configserver.dto.LoginResponse;
import com.saguro.rapid.configserver.dto.PermissionSummaryDTO;
import com.saguro.rapid.configserver.dto.UserInfo;
import com.saguro.rapid.configserver.entity.User;
import com.saguro.rapid.configserver.entity.UserPermission;
import com.saguro.rapid.configserver.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserPermissionService userPermissionService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
            JwtService jwtTokenProvider,
            UserRepository userRepository,
            UserPermissionService userPermissionService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.userPermissionService = userPermissionService;
    }

    @Override
    public LoginResponse authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtTokenProvider.generateToken(authentication);

        // Check if user is admin
        boolean isAdmin = userPermissionService.isAdmin(username);

        // Get user permissions
        List<PermissionSummaryDTO> permissions = new ArrayList<>();
        if (!isAdmin) {
            // Only fetch permissions if user is not admin (admin has all permissions)
            List<UserPermission> userPermissions = userPermissionService.getPermissionsByUsername(username);
            permissions = userPermissions.stream()
                    .map(perm -> new PermissionSummaryDTO(
                            perm.getOrganization() != null ? perm.getOrganization().getId() : null,
                            perm.getOrganization() != null ? perm.getOrganization().getName() : null,
                            perm.getApplication() != null ? perm.getApplication().getId() : null,
                            perm.getApplication() != null ? perm.getApplication().getName() : null,
                            perm.isCanRead(),
                            perm.isCanCreate(),
                            perm.isCanUpdate(),
                            perm.isCanDelete()))
                    .collect(Collectors.toList());
        }

        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()),
                isAdmin,
                permissions);

        return new LoginResponse(token, userInfo);
    }
}
