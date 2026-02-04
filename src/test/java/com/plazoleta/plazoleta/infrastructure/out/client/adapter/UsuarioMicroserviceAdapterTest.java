package com.plazoleta.plazoleta.infrastructure.out.client.adapter;

import com.plazoleta.plazoleta.domain.exception.ServicioUsuarioNoDisponibleException;
import com.plazoleta.plazoleta.domain.exception.UsuarioNoEncontradoException;
import com.plazoleta.plazoleta.domain.model.UsuarioModelo;
import com.plazoleta.plazoleta.infraestructure.out.client.adapter.UsuarioMicroserviceAdapter;
import com.plazoleta.plazoleta.infraestructure.out.client.dto.UsuarioResponseDto;
import com.plazoleta.plazoleta.infraestructure.out.client.feign.UsuarioFeignClient;
import com.plazoleta.plazoleta.infraestructure.out.client.mapper.UsuarioClientMapper;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - UserMicroserviceAdapter")
class UsuarioMicroserviceAdapterTest {

    @Mock
    private UsuarioFeignClient userFeignClient;

    @Mock
    private UsuarioClientMapper userClientMapper;

    private UsuarioMicroserviceAdapter adapter;

    private UsuarioResponseDto userResponseDto;
    private UsuarioModelo userModel;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioMicroserviceAdapter(userFeignClient, userClientMapper);

        userResponseDto = new UsuarioResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setRol("PROPIETARIO");

        userModel = new UsuarioModelo();
        userModel.setId(1L);
        userModel.setRole("PROPIETARIO");
    }

    @Test
    @DisplayName("Debe obtener usuario exitosamente cuando existe")
    void shouldGetUserSuccessfullyWhenExists() {
        // Arrange
        when(userFeignClient.getUserById(1L)).thenReturn(userResponseDto);
        when(userClientMapper.toUserModel(userResponseDto)).thenReturn(userModel);

        // Act
        UsuarioModelo result = adapter.getUserById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRole()).isEqualTo("PROPIETARIO");
        verify(userFeignClient, times(1)).getUserById(1L);
        verify(userClientMapper, times(1)).toUserModel(userResponseDto);
    }

    @Test
    @DisplayName("Debe lanzar UsuarioNoEncontradoException cuando el usuario no existe (404)")
    void shouldThrowUsuarioNoEncontradoWhenUserNotFound() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/usuarios/999",
                new HashMap<>(),
                null,
                new RequestTemplate()
        );
        FeignException.NotFound notFoundException = new FeignException.NotFound(
                "User not found",
                request,
                null,
                null
        );
        when(userFeignClient.getUserById(999L)).thenThrow(notFoundException);

        assertThatThrownBy(() -> adapter.getUserById(999L))
                .isInstanceOf(UsuarioNoEncontradoException.class)
                .hasMessage("El usuario no existe");
        verify(userFeignClient, times(1)).getUserById(999L);
        verify(userClientMapper, never()).toUserModel(any());
    }

    @Test
    @DisplayName("Debe lanzar ServicioUsuarioNoDisponibleException cuando Feign falla")
    void shouldThrowServicioUsuarioNoDisponibleWhenFeignThrowsOtherException() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/usuarios/1",
                new HashMap<>(),
                null,
                new RequestTemplate()
        );
        FeignException feignException = new FeignException.InternalServerError(
                "Internal Server Error",
                request,
                null,
                null
        );
        when(userFeignClient.getUserById(1L)).thenThrow(feignException);

        assertThatThrownBy(() -> adapter.getUserById(1L))
                .isInstanceOf(ServicioUsuarioNoDisponibleException.class)
                .hasMessageContaining("Error al comunicarse con el microservicio de usuarios");

        verify(userFeignClient, times(1)).getUserById(1L);
        verify(userClientMapper, never()).toUserModel(any());
    }

    @Test
    @DisplayName("Debe pasar el ID Long al Feign client")
    void shouldPassLongIdToFeignClient() {
        // Arrange
        when(userFeignClient.getUserById(100L)).thenReturn(userResponseDto);
        when(userClientMapper.toUserModel(any())).thenReturn(userModel);

        // Act
        adapter.getUserById(100L);

        // Assert
        verify(userFeignClient, times(1)).getUserById(100L);
    }

    @Test
    @DisplayName("Debe delegar el mapeo al UserClientMapper")
    void shouldDelegateMappingToUserClientMapper() {
        // Arrange
        when(userFeignClient.getUserById(any())).thenReturn(userResponseDto);
        when(userClientMapper.toUserModel(userResponseDto)).thenReturn(userModel);

        // Act
        adapter.getUserById(1L);

        // Assert
        verify(userClientMapper).toUserModel(userResponseDto);
        verifyNoMoreInteractions(userClientMapper);
    }

    @Test
    @DisplayName("Debe manejar múltiples llamadas independientes")
    void shouldHandleMultipleIndependentCalls() {
        // Arrange
        when(userFeignClient.getUserById(1L)).thenReturn(userResponseDto);
        when(userFeignClient.getUserById(2L)).thenReturn(userResponseDto);
        when(userClientMapper.toUserModel(any())).thenReturn(userModel);

        // Act
        adapter.getUserById(1L);
        adapter.getUserById(2L);

        // Assert
        verify(userFeignClient, times(1)).getUserById(1L);
        verify(userFeignClient, times(1)).getUserById(2L);
        verify(userClientMapper, times(2)).toUserModel(any());
    }
}