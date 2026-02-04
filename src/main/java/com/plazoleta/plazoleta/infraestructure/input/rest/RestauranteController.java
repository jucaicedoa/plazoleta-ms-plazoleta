package com.plazoleta.plazoleta.infraestructure.input.rest;

import com.plazoleta.plazoleta.application.dto.CrearRestauranteRequestDto;
import com.plazoleta.plazoleta.application.handler.IRestauranteHandler;
import com.plazoleta.plazoleta.infraestructure.exceptionhandler.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurantes")
@RequiredArgsConstructor
@Tag(name = "Restaurantes", description = "API para gestión de restaurantes")
public class RestauranteController {

    private final IRestauranteHandler restauranteHandler;

    @PostMapping
    @Operation(
            summary = "Crear restaurante",
            description = "Crea un nuevo restaurante validando que el propietario exista y tenga el rol adecuado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario no es propietario",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Rol no autorizado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "502", description = "Servicio de usuarios no disponible",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<Void> create(@RequestBody CrearRestauranteRequestDto dto) {
        restauranteHandler.createRestaurante(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}