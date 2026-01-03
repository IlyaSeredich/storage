package com.cloud.cloudstorage.service.impl;

import com.cloud.cloudstorage.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;

    @Override
    public void authenticate(String username, String password, HttpServletRequest request) {
        Authentication authentication = getAuthentication(username,password);
        createSession(authentication, request);
    }

    private Authentication getAuthentication(String username, String password) {
        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        username,
                        password
                ));
    }

    private void createSession(Authentication authentication, HttpServletRequest request) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }
}
