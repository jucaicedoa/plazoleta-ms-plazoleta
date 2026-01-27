package com.plazoleta.plazoleta.infraestructure.input.rest;

import com.plazoleta.plazoleta.application.dto.CrearRestauranteRequestDto;
import com.plazoleta.plazoleta.application.handler.CrearRestauranteHandler;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/restaurantes")
@RequiredArgsConstructor
@Tag(name = "Restaurantes", description = "API para gestión de restaurantes")
public class RestauranteController {

    private final CrearRestauranteHandler handler;

    @PostMapping
    @Operation(
            summary = "Crear restaurante",
            description = "Crea un nuevo restaurante validando que el propietario exista y tenga el rol adecuado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario no es propietario"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> create(@RequestBody CrearRestauranteRequestDto dto) {
        handler.handle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}