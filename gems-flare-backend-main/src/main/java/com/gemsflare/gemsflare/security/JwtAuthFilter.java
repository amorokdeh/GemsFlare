package com.gemsflare.gemsflare.security;

import com.gemsflare.gemsflare.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthFilter extends HttpFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String requestURI = request.getRequestURI();

        if (
            //only the exact routes
            requestURI.equals("/user/getAllUsers") ||
            requestURI.equals("/user/deleteMyUser") ||
            requestURI.equals("user/deleteUserByAdmin") ||
            requestURI.equals("/testDb") ||
            requestURI.equals("/checkToken") ||

            //all routes under (**)
            requestURI.startsWith("/permission/") ||
            requestURI.startsWith("/deliveryAddress/") ||
            requestURI.startsWith("/billAddress/")
        )
        {

            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (token == null || !token.startsWith("Bearer ")) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Missing or invalid token");
                return;
            }

            token = token.substring(7);

            String validatedToken = jwtUtil.validateAndRenewToken(token);

            if (validatedToken == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid or expired token");
                return;
            }

            if (!validatedToken.equals(token)) {
                response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + validatedToken);
            }

            UUID userId = jwtUtil.getUserIdFromToken(validatedToken);
            Optional<?> user = userRepository.findById(userId);

            if (user.isEmpty()) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("User not found");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}