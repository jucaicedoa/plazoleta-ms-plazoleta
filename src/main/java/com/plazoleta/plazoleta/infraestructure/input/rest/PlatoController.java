package com.plazoleta.plazoleta.infraestructure.input.rest;

import com.plazoleta.plazoleta.application.dto.ActualizarPlatoRequestDto;
import com.plazoleta.plazoleta.application.dto.CrearPlatoRequestDto;
import com.plazoleta.plazoleta.application.handler.IPlatoHandler;
import com.plazoleta.plazoleta.infraestructure.exceptionhandler.dto.ErrorResponseDto;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
@Tag(name = "Platos", description = "API para gestión de platos de restaurantes")
public class PlatoController {

    private final IPlatoHandler platoHandler;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear un nuevo plato",
            description = "Permite a un propietario crear un plato asociado a su restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plato creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "El usuario no es propietario del restaurante",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario o restaurante no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<Void> createDish(
            @Parameter(description = "ID del propietario", required = true)
            @RequestHeader("propietario-id") Long propietarioId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del plato a crear",
                    required = true
            )
            @RequestBody CrearPlatoRequestDto dto) {
        platoHandler.createDish(dto, propietarioId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Modificar un plato",
            description = "Permite a un propietario modificar únicamente el precio y la descripción de un plato"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plato modificado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "El usuario no es propietario del restaurante",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Plato o usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<Void> updateDish(
            @Parameter(description = "ID del plato", required = true)
            @PathVariable("id") Long platoId,
            @Parameter(description = "ID del propietario", required = true)
            @RequestHeader("propietario-id") Long propietarioId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos permitidos a modificar (precio y descripcion)",
                    required = true
            )
            @RequestBody ActualizarPlatoRequestDto dto) {
        platoHandler.updateDish(platoId, dto, propietarioId);
        return ResponseEntity.ok().build();
    }
}