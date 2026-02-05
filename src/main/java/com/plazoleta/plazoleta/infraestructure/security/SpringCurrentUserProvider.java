package com.plazoleta.plazoleta.infraestructure.security;

import com.plazoleta.plazoleta.application.security.ICurrentUserProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Implementación que obtiene userId y role del SecurityContext (poblado por JwtAuthenticationFilter)
 */
public class SpringCurrentUserProvider implements ICurrentUserProvider {

    @Override
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }

        if (principal instanceof String userIdStr) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getAuthorities() == null
                || auth.getAuthorities().isEmpty()) {
            return null;
        }
        return auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    }
}