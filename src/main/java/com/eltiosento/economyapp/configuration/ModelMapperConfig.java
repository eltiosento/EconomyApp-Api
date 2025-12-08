package com.eltiosento.economyapp.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Declarar el ModelMapper com a @Bean dins d'una classe @Configuration ens permet injectar-lo fàcilment a qualsevol classe
 */
@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
