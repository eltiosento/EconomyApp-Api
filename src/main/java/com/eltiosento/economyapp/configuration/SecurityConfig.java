package com.eltiosento.economyapp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.eltiosento.economyapp.jwt.JwtAuthenticationException;
import com.eltiosento.economyapp.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
// @EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthenticationProvider authProvider;

    @Autowired
    private JwtAuthenticationException jwtAuthenticationException;

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/swagger-resources",
            "/swagger-ui.html"
    };

    // Per a protegir els endpoints d'una forma centralitzada podem posar la ruta
    // que volem protegir o el prefixe i després amb el mètode hasAuthority("'ROL'")
    // indiquem que només els usuaris amb aquest rol podran accedir a aquesta ruta.
    // També podem protegir els endpoints al Controlador habilitant l'anotació
    // @EnableMethodSecurity en aquesta classe "SecurityConfig" i posant la
    // anotació @PreAuthorize("hasAuthority('ROL')") en el mètode del Controlador.

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        authRequest -> authRequest
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/api/media/**")
                                .permitAll()
                                // Rutes GET i PUT per a USER, ADMIN i SUPERADMIN
                                .requestMatchers(HttpMethod.GET, "/api/**")
                                .hasAnyAuthority("USER", "ADMIN", "SUPERADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/user/**")
                                .hasAnyAuthority("USER", "ADMIN", "SUPERADMIN")
                                // Rutes POST, PUT i DELETE només per a ADMIN i
                                // SUPERADMIN
                                .requestMatchers(HttpMethod.POST, "/api/**")
                                .hasAnyAuthority("ADMIN", "SUPERADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/**")
                                .hasAnyAuthority("ADMIN", "SUPERADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/**")
                                .hasAnyAuthority("ADMIN", "SUPERADMIN")
                                // Rutes d'administració només per SUPERADMIN
                                .requestMatchers("/api/users")
                                .hasAuthority("SUPERADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/user/**")
                                .hasAuthority(
                                        "SUPERADMIN")
                                .anyRequest()
                                .authenticated())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(
                        authProvider)
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                jwtAuthenticationException))
                .build();

    }
    // Flutter Web (en el navegadors) fa una petició OPTIONS prèvia abans de la GET
    // /user/{id} per validar el CORS.
    // Aquesta preflight no porta el token JWT.
    // El endpoint /api/user/{id} estava protegit amb .authenticated(), així que
    // Spring rebutjava la preflight request amb un 401 o 403.
    // El navegador veia que no hi havia resposta correcta a la preflight i mostrava
    // l'error CORS, però el problema era realment d'autenticació.
    // SOLUCIÓ: afegir la línia
    // .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() -> Si és una petició
    // OPTIONS, deixa-la passar que només vol comprovar si pot continuar. Mal de cap
    // per resoldre problemes de cors amb flutter web

    // .authenticated()) per a protegir tots els endpoints ara per ferproves deixem
    // tot obert .permitAll()
}
