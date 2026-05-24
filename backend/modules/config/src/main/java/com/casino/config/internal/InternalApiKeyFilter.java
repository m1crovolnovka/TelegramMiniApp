package com.casino.config.internal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private final InternalApiProperties internalApiProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !path.startsWith("/api/internal/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String provided = request.getHeader("X-Internal-Api-Key");
        String expected = internalApiProperties.getApiKey();
        if (expected == null
                || expected.isBlank()
                || provided == null
                || !expected.equals(provided)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid internal API key");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
