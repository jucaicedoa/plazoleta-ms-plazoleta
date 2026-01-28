package com.plazoleta.plazoleta.application.mapper;

import com.plazoleta.plazoleta.application.dto.CrearRestauranteRequestDto;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestauranteApplicationMapper {

    @Mapping(target = "id", ignore = true)
    Restaurante toDomain(CrearRestauranteRequestDto dto);
}