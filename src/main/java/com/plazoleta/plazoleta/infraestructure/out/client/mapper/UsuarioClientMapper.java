package com.plazoleta.plazoleta.infraestructure.out.client.mapper;

import com.plazoleta.plazoleta.domain.model.UsuarioModelo;
import com.plazoleta.plazoleta.infraestructure.out.client.dto.UsuarioResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioClientMapper {

    UsuarioModelo toUserModel(UsuarioResponseDto dto);

}