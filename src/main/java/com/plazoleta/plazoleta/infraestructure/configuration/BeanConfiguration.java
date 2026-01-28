package com.plazoleta.plazoleta.infraestructure.configuration;

import com.plazoleta.plazoleta.domain.api.PlatoServicePort;
import com.plazoleta.plazoleta.domain.api.RestauranteServicePort;
import com.plazoleta.plazoleta.domain.spi.PlatoPersistencePort;
import com.plazoleta.plazoleta.domain.spi.RestaurantePersistencePort;
import com.plazoleta.plazoleta.domain.spi.RestauranteValidationPort;
import com.plazoleta.plazoleta.domain.spi.UsuarioValidationPort;
import com.plazoleta.plazoleta.domain.usecase.CrearPlatoUseCase;
import com.plazoleta.plazoleta.domain.usecase.CrearRestauranteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final RestaurantePersistencePort restaurantePersistencePort;
    private final UsuarioValidationPort usuarioValidationPort;
    private final PlatoPersistencePort platoPersistencePort;
    private final RestauranteValidationPort restauranteValidationPort;

    @Bean
    public RestauranteServicePort restauranteServicePort() {
        return new CrearRestauranteUseCase(restaurantePersistencePort, usuarioValidationPort);
    }

    @Bean
    public PlatoServicePort dishServicePort() {
        return new CrearPlatoUseCase(
                platoPersistencePort,
                usuarioValidationPort,
                restauranteValidationPort
        );
    }
}