package com.plazoleta.plazoleta.infraestructure.out.client.adapter;

import com.plazoleta.plazoleta.domain.exception.ServicioUsuarioNoDisponibleException;
import com.plazoleta.plazoleta.domain.exception.UsuarioNoEncontradoException;
import com.plazoleta.plazoleta.domain.model.UsuarioModelo;
import com.plazoleta.plazoleta.domain.spi.UsuarioValidationPort;
import com.plazoleta.plazoleta.infraestructure.out.client.dto.UsuarioResponseDto;
import com.plazoleta.plazoleta.infraestructure.out.client.feign.UsuarioFeignClient;
import com.plazoleta.plazoleta.infraestructure.out.client.mapper.UsuarioClientMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UsuarioMicroserviceAdapter implements UsuarioValidationPort {

    private final UsuarioFeignClient userFeignClient;
    private final UsuarioClientMapper userClientMapper;

    @Override
    public UsuarioModelo getUserById(Long userId) {
        try {
            log.info("Llamando al microservicio de usuarios para obtener usuario con ID: {}", userId);
            UsuarioResponseDto response = userFeignClient.getUserById(userId);
            log.info("Usuario obtenido exitosamente: ID={}, Rol={}", response.getId(), response.getRol());
            return userClientMapper.toUserModel(response);
        } catch (FeignException.NotFound e) {
            log.warn("Usuario con ID {} no encontrado en el microservicio de usuarios (404)", userId);
            throw new UsuarioNoEncontradoException("El usuario no existe");
        } catch (FeignException e) {
            log.error("Error al comunicarse con el microservicio de usuarios: Status={}, Message={}",
                    e.status(), e.getMessage());
            throw new ServicioUsuarioNoDisponibleException(
                    "Error al comunicarse con el microservicio de usuarios: " + e.getMessage(), e);
        }
    }
}