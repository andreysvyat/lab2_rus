package com.example.clinic;


import com.example.clinic.dto.UserDetailsDto;
import com.example.clinic.security.JwtUtils;
import com.example.clinic.security.SecurityHelper;
import com.example.clinic.security.SecurityHelperClass;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";
    private final SecurityHelper securityHelper = new SecurityHelperClass();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return null;
        }

        Claims parsedClaims = parseClaims(token);
        if (parsedClaims == null || isTokenExpired(parsedClaims)) {
            return null;
        }
        UsernamePasswordAuthenticationToken authenticationToken = createAuthenticationToken(parsedClaims, token);
        return authenticationToken;
    }

    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER);
        String nonNullPrefix = PREFIX;
        if (StringUtils.hasLength(token) && token.startsWith(nonNullPrefix)) {
            return token.replace(nonNullPrefix, "").trim();
        }
        return null;
    }

    private Claims parseClaims(String token) {
        return JwtUtils.getAccessClaims(token, securityHelper.getJwtValidationKey());
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(Claims claims, String token) {
        String subject = claims.getSubject();

        Long userId = ((Integer) claims.get("id")).longValue();
        System.out.println(claims);


        Boolean isEnabled = (Boolean) claims.get("enabled");
        if (isEnabled == null || !isEnabled) {
            return null;
        }

        List<SimpleGrantedAuthority> authorities = ((List<?>) claims.get("roles"))
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(
                new UserDetailsDto(userId, authorities, true, true, true, isEnabled),
                token,
                authorities
        );
    }
}