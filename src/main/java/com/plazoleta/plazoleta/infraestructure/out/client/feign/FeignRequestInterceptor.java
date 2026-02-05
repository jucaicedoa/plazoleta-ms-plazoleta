package com.plazoleta.plazoleta.infraestructure.out.client.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null && !authorization.isBlank()) {
                template.header(HttpHeaders.AUTHORIZATION, authorization);
            }
        }
    }
}