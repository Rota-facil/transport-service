package com.rota.facil.transport_service.security.filters;

import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String id = request.getHeader("x-user-id");
        String email = request.getHeader("x-user-email");
        String role = request.getHeader("x-user-role");
        String prefectureId = request.getHeader("x-prefecture-id");

        if(id == null || email == null || role == null || prefectureId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        CurrentUser currentUser = new CurrentUser(UUID.fromString(id), UUID.fromString(prefectureId), email, role);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority("ROLE_" + currentUser.role())));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
