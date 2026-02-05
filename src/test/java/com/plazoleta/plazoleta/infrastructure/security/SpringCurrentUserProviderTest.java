package com.plazoleta.plazoleta.infrastructure.security;

import com.plazoleta.plazoleta.infraestructure.security.SpringCurrentUserProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests - SpringCurrentUserProvider")
class SpringCurrentUserProviderTest {

    private SpringCurrentUserProvider provider;

    @BeforeEach
    void setUp() {
        provider = new SpringCurrentUserProvider();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Debe retornar null cuando no hay autenticación")
    void shouldReturnNullWhenNoAuthentication() {
        SecurityContextHolder.clearContext();

        assertThat(provider.getCurrentUserId()).isNull();
        assertThat(provider.getCurrentUserRole()).isNull();
    }

    @Test
    @DisplayName("Debe retornar userId cuando el principal es Long")
    void shouldReturnUserIdWhenPrincipalIsLong() {
        Long userId = 10L;
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_PROPIETARIO"));
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(provider.getCurrentUserId()).isEqualTo(10L);
        assertThat(provider.getCurrentUserRole()).isEqualTo("PROPIETARIO");
    }

    @Test
    @DisplayName("Debe retornar userId cuando el principal es String numérico")
    void shouldReturnUserIdWhenPrincipalIsNumericString() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("42", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(provider.getCurrentUserId()).isEqualTo(42L);
        assertThat(provider.getCurrentUserRole()).isEqualTo("ADMINISTRADOR");
    }

    @Test
    @DisplayName("Debe retornar null cuando el principal String no es número")
    void shouldReturnNullWhenPrincipalStringIsNotNumber() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("invalid", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(provider.getCurrentUserId()).isNull();
    }

    @Test
    @DisplayName("Debe retornar null cuando no hay authorities")
    void shouldReturnNullRoleWhenNoAuthorities() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(1L, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(provider.getCurrentUserId()).isEqualTo(1L);
        assertThat(provider.getCurrentUserRole()).isNull();
    }

    @Test
    @DisplayName("Debe quitar prefijo ROLE_ del rol")
    void shouldStripRolePrefix() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(5L, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLEADO")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(provider.getCurrentUserRole()).isEqualTo("EMPLEADO");
    }

    @Test
    @DisplayName("Debe retornar null cuando la autenticación no está autenticada")
    void shouldReturnNullWhenNotAuthenticated() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(1L, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        auth.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(provider.getCurrentUserId()).isNull();
        assertThat(provider.getCurrentUserRole()).isNull();
    }

    @Test
    @DisplayName("Debe retornar null cuando el principal es null")
    void shouldReturnNullWhenPrincipalIsNull() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(null, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThat(provider.getCurrentUserId()).isNull();
    }
}