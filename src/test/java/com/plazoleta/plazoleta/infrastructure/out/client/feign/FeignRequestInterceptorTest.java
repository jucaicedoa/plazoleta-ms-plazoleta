package com.plazoleta.plazoleta.infrastructure.out.client.feign;

import com.plazoleta.plazoleta.infraestructure.out.client.feign.FeignRequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Tests - FeignRequestInterceptor")
class FeignRequestInterceptorTest {

    private FeignRequestInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new FeignRequestInterceptor();
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Debe agregar header Authorization cuando hay request con token")
    void shouldAddAuthorizationHeaderWhenRequestHasToken() {
        String token = "Bearer jwt-token-value";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertThat(template.headers().get(HttpHeaders.AUTHORIZATION))
                .isNotNull()
                .containsExactly(token);
    }

    @Test
    @DisplayName("No debe agregar header cuando no hay request attributes")
    void shouldNotAddHeaderWhenNoRequestAttributes() {
        RequestContextHolder.resetRequestAttributes();

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertThat(template.headers().get(HttpHeaders.AUTHORIZATION)).isNull();
    }

    @Test
    @DisplayName("No debe agregar header cuando Authorization es null")
    void shouldNotAddHeaderWhenAuthorizationIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertThat(template.headers().get(HttpHeaders.AUTHORIZATION)).isNull();
    }

    @Test
    @DisplayName("No debe agregar header cuando Authorization está en blanco")
    void shouldNotAddHeaderWhenAuthorizationIsBlank() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("   ");

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertThat(template.headers().get(HttpHeaders.AUTHORIZATION)).isNull();
    }

    @Test
    @DisplayName("Debe agregar header cuando Authorization tiene valor")
    void shouldAddHeaderWhenAuthorizationHasValue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer abc.def.ghi");

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        assertThat(template.headers().get(HttpHeaders.AUTHORIZATION))
                .containsExactly("Bearer abc.def.ghi");
    }
}