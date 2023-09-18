package com.garganttua.server.modules.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration	
public class GGServerApi {

	@Bean
	@Primary
    public OpenAPI openApi() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Garganttua Server API").description(""));
    }

}
