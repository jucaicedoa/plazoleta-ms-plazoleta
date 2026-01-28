package com.plazoleta.plazoleta.infraestructure.configuration;

import com.plazoleta.plazoleta.domain.api.RestauranteServicePort;
import com.plazoleta.plazoleta.domain.spi.RestaurantePersistencePort;
import com.plazoleta.plazoleta.domain.spi.UsuarioValidationPort;
import com.plazoleta.plazoleta.domain.usecase.CrearRestauranteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final RestaurantePersistencePort restaurantPersistencePort;
    private final UsuarioValidationPort userValidationPort;

    @Bean
    public RestauranteServicePort restaurantServicePort() {
        return new CrearRestauranteUseCase(restaurantPersistencePort, userValidationPort);
    }
}