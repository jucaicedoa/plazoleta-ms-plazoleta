package com.plazoleta.plazoleta.infraestructure.out.client.feign;

import com.plazoleta.plazoleta.infraestructure.out.client.dto.UsuarioResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${feign.client.config.user-service.url}")
public interface UsuarioFeignClient {

    @GetMapping("/api/v1/usuarios/{id}")
    UsuarioResponseDto getUserById(@PathVariable("id") Long id);
}