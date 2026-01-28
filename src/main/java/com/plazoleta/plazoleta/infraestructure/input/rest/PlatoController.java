package com.plazoleta.plazoleta.infraestructure.input.rest;

import com.plazoleta.plazoleta.application.dto.CrearPlatoRequestDto;
import com.plazoleta.plazoleta.application.handler.CrearPlatoHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platos")
@RequiredArgsConstructor
@Tag(name = "Platos", description = "API para gestión de platos de restaurantes")
public class PlatoController {

    private final CrearPlatoHandler crearPlatoHandler;

    @PostMapping
    @Operation(summary = "Crear un nuevo plato",
            description = "Permite a un propietario crear un plato asociado a su restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plato creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "El usuario no es propietario del restaurante",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<Void> createDish(
            @Parameter(description = "ID del propietario", required = true)
            @RequestHeader("propietario-id") Long propietarioId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del plato a crear",
                    required = true
            )
            @RequestBody CrearPlatoRequestDto dto) {

        crearPlatoHandler.handle(dto, propietarioId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}