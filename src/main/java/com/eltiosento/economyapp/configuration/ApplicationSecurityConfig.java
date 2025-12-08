package com.eltiosento.economyapp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eltiosento.economyapp.repository.UserRepository;

@Configuration
public class ApplicationSecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*
     * @Bean
     * AuthenticationProvider authenticationProvider() {
     * 
     * DaoAuthenticationProvider daoAuthenticationProvider = new
     * DaoAuthenticationProvider();
     * 
     * daoAuthenticationProvider.setUserDetailsService(userDetailsService());
     * daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
     * 
     * return daoAuthenticationProvider;
     * }
     */

    @Bean
    AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        // Inyectamos el UserDetailsService en el constructor
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        // Luego configuramos el PasswordEncoder
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService() {

        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

}
