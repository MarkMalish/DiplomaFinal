package com.example.eventmanager.security.oauth;

import com.example.eventmanager.model.Role;
import com.example.eventmanager.model.User;
import com.example.eventmanager.repository.RoleRepository;
import com.example.eventmanager.repository.UserRepository;
import com.example.eventmanager.security.jwt.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    private final RoleRepository roleRepository;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtUtils jwtUtils, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.roleRepository = roleRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(email.split("@")[0]);
                    newUser.setEmail(email);
                    newUser.setPassword("oauth2-user"); // not used
                    Role roleUser = roleRepository.findByName("USER")
                            .orElseThrow(() -> new RuntimeException("Role USER not found"));

                    newUser.setRoles(Collections.singleton(roleUser));
                    return userRepository.save(newUser);
                });

        String token = jwtUtils.generateToken(user); //
        response.sendRedirect("http://localhost:3000/oauth2-success?token=" + token);
    }
}
