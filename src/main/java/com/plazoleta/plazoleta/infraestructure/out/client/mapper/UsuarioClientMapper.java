package com.plazoleta.plazoleta.infraestructure.out.client.mapper;

import com.plazoleta.plazoleta.domain.model.UsuarioModelo;
import com.plazoleta.plazoleta.infraestructure.out.client.dto.UsuarioResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioClientMapper {

    @Mapping(source = "rol", target = "role")
    UsuarioModelo toUserModel(UsuarioResponseDto dto);

}