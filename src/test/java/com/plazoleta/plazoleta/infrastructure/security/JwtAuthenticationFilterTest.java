package com.plazoleta.plazoleta.infrastructure.security;

import com.plazoleta.plazoleta.infraestructure.security.JwtAuthenticationFilter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("Tests - JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    private static final String JWT_SECRET = "test-secret-key-minimum-256-bits-for-hs256-algorithm";

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter();
        ReflectionTestUtils.setField(filter, "jwtSecret", JWT_SECRET);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Debe continuar la cadena cuando no hay header Authorization")
    void shouldContinueChainWhenNoAuthHeader() throws ServletException, IOException {
        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(eq(request), eq(response));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Debe continuar la cadena cuando el header no empieza con Bearer")
    void shouldContinueChainWhenHeaderNotBearer() throws ServletException, IOException {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic abc123");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(eq(request), eq(response));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Debe establecer autenticación cuando el token es válido")
    void shouldSetAuthenticationWhenTokenIsValid() throws ServletException, IOException {
        String token = createValidToken(1L, "PROPIETARIO");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(eq(request), eq(response));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(1L);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .anyMatch(a -> "ROLE_PROPIETARIO".equals(a.getAuthority()));
    }

    @Test
    @DisplayName("Debe usar rol USER por defecto cuando el claim role es null")
    void shouldUseDefaultRoleWhenRoleClaimIsNull() throws ServletException, IOException {
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("2")
                .signWith(key)
                .compact();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(eq(request), eq(response));
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .anyMatch(a -> "ROLE_USER".equals(a.getAuthority()));
    }

    @Test
    @DisplayName("Debe mantener ROLE_ si el claim ya tiene el prefijo")
    void shouldKeepRolePrefixWhenAlreadyPresent() throws ServletException, IOException {
        String token = createValidToken(3L, "ROLE_ADMINISTRADOR");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .anyMatch(a -> "ROLE_ADMINISTRADOR".equals(a.getAuthority()));
    }

    @Test
    @DisplayName("Debe continuar la cadena cuando subject está vacío")
    void shouldContinueChainWhenSubjectIsEmpty() throws ServletException, IOException {
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("")
                .claim("role", "USER")
                .signWith(key)
                .compact();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(eq(request), eq(response));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Debe retornar 401 cuando el token está expirado")
    void shouldReturn401WhenTokenExpired() throws ServletException, IOException {
        String token = createExpiredToken(1L, "PROPIETARIO");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).isEqualTo("Token expirado");
    }

    @Test
    @DisplayName("Debe retornar 401 cuando el token es inválido")
    void shouldReturn401WhenTokenInvalid() throws ServletException, IOException {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token-invalido");

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).isEqualTo("Token inválido");
    }

    @Test
    @DisplayName("Debe retornar 401 cuando el subject no es un número")
    void shouldReturn401WhenSubjectIsNotNumber() throws ServletException, IOException {
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("not-a-number")
                .claim("role", "USER")
                .signWith(key)
                .compact();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).isEqualTo("Token inválido");
    }

    private String createValidToken(Long userId, String role) {
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .signWith(key)
                .compact();
    }

    private String createExpiredToken(Long userId, String role) {
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .expiration(new Date(System.currentTimeMillis() - 10000))
                .signWith(key)
                .compact();
    }
}