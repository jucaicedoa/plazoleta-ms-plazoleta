package com.plazoleta.plazoleta.application.security;

/**
 * Puerto para obtener el usuario autenticado actual (desde el contexto de seguridad JWT).
 * La aplicación (handlers) usa esta interfaz para no depender de Spring Security.
 */
public interface ICurrentUserProvider {

    Long getCurrentUserId();

    String getCurrentUserRole();
}