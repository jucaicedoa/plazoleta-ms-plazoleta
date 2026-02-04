package com.plazoleta.plazoleta.infraestructure.exceptionhandler.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de error de la API")
public class ErrorResponseDto {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Fecha y hora del error")
    private LocalDateTime timestamp;

    @Schema(description = "Código HTTP")
    private int status;

    @Schema(description = "Tipo de error")
    private String error;

    @Schema(description = "Mensaje descriptivo")
    private String message;
}