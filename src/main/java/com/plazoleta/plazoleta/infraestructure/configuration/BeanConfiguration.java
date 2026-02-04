package com.plazoleta.plazoleta.infraestructure.configuration;

import com.plazoleta.plazoleta.application.handler.IPlatoHandler;
import com.plazoleta.plazoleta.application.handler.IRestauranteHandler;
import com.plazoleta.plazoleta.application.handler.PlatoHandler;
import com.plazoleta.plazoleta.application.handler.RestauranteHandler;
import com.plazoleta.plazoleta.application.mapper.PlatoApplicationMapper;
import com.plazoleta.plazoleta.application.mapper.RestauranteApplicationMapper;
import com.plazoleta.plazoleta.domain.api.PlatoServicePort;
import com.plazoleta.plazoleta.domain.api.PlatoUpdateServicePort;
import com.plazoleta.plazoleta.domain.api.RestauranteServicePort;
import com.plazoleta.plazoleta.domain.spi.PlatoPersistencePort;
import com.plazoleta.plazoleta.domain.spi.RestaurantePersistencePort;
import com.plazoleta.plazoleta.domain.spi.RestauranteValidationPort;
import com.plazoleta.plazoleta.domain.spi.UsuarioValidationPort;
import com.plazoleta.plazoleta.domain.usecase.ActualizarPlatoUseCase;
import com.plazoleta.plazoleta.domain.usecase.CrearPlatoUseCase;
import com.plazoleta.plazoleta.domain.usecase.CrearRestauranteUseCase;
import com.plazoleta.plazoleta.infraestructure.out.client.adapter.UsuarioMicroserviceAdapter;
import com.plazoleta.plazoleta.infraestructure.out.client.feign.UsuarioFeignClient;
import com.plazoleta.plazoleta.infraestructure.out.client.mapper.UsuarioClientMapper;
import com.plazoleta.plazoleta.infraestructure.out.jpa.adapter.PlatoJpaAdapter;
import com.plazoleta.plazoleta.infraestructure.out.jpa.adapter.RestauranteJpaAdapter;
import com.plazoleta.plazoleta.infraestructure.out.jpa.adapter.RestauranteValidationJpaAdapter;
import com.plazoleta.plazoleta.infraestructure.out.jpa.mapper.PlatoEntityMapper;
import com.plazoleta.plazoleta.infraestructure.out.jpa.mapper.RestauranteEntityMapper;
import com.plazoleta.plazoleta.infraestructure.out.jpa.repository.PlatoRepository;
import com.plazoleta.plazoleta.infraestructure.out.jpa.repository.RestauranteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public PlatoPersistencePort platoPersistencePort(PlatoRepository platoRepository,
                                                     PlatoEntityMapper platoEntityMapper) {
        return new PlatoJpaAdapter(platoRepository, platoEntityMapper);
    }

    @Bean
    public RestaurantePersistencePort restaurantePersistencePort(RestauranteRepository restauranteRepository,
                                                                RestauranteEntityMapper restauranteEntityMapper) {
        return new RestauranteJpaAdapter(restauranteRepository, restauranteEntityMapper);
    }

    @Bean
    public RestauranteValidationPort restauranteValidationPort(RestauranteRepository restauranteRepository) {
        return new RestauranteValidationJpaAdapter(restauranteRepository);
    }

    @Bean
    public UsuarioValidationPort usuarioValidationPort(UsuarioFeignClient usuarioFeignClient,
                                                       UsuarioClientMapper usuarioClientMapper) {
        return new UsuarioMicroserviceAdapter(usuarioFeignClient, usuarioClientMapper);
    }

    @Bean
    public RestauranteServicePort restauranteServicePort(RestaurantePersistencePort restaurantePersistencePort,
                                                         UsuarioValidationPort usuarioValidationPort) {
        return new CrearRestauranteUseCase(restaurantePersistencePort, usuarioValidationPort);
    }

    @Bean
    public PlatoServicePort platoServicePort(PlatoPersistencePort platoPersistencePort,
                                             UsuarioValidationPort usuarioValidationPort,
                                             RestauranteValidationPort restauranteValidationPort) {
        return new CrearPlatoUseCase(platoPersistencePort, usuarioValidationPort, restauranteValidationPort);
    }

    @Bean
    public PlatoUpdateServicePort platoUpdateServicePort(PlatoPersistencePort platoPersistencePort,
                                                         UsuarioValidationPort usuarioValidationPort,
                                                         RestauranteValidationPort restauranteValidationPort) {
        return new ActualizarPlatoUseCase(platoPersistencePort, usuarioValidationPort, restauranteValidationPort);
    }

    @Bean
    public IPlatoHandler platoHandler(PlatoServicePort platoServicePort,
                                     PlatoUpdateServicePort platoUpdateServicePort,
                                     PlatoApplicationMapper platoApplicationMapper) {
        return new PlatoHandler(platoServicePort, platoUpdateServicePort, platoApplicationMapper);
    }

    @Bean
    public IRestauranteHandler restauranteHandler(RestauranteServicePort restauranteServicePort,
                                                 RestauranteApplicationMapper restauranteApplicationMapper) {
        return new RestauranteHandler(restauranteServicePort, restauranteApplicationMapper);
    }
}
