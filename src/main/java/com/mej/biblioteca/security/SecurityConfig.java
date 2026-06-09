package com.mej.biblioteca.security;

import com.mej.biblioteca.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Value("${app.cors.allowed-origins}")
    private List<String> corsAllowedOrigins;

    @SuppressWarnings("java:S4502") // CSRF desabilitado é seguro aqui pois a API é Stateless e usa JWT
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/cadastro", "/auth/cadastro/confirmar",
                                "/auth/reenviar-codigo", "/auth/login")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/senha/solicitar-alteracao",
                                "/auth/senha/confirmar-alteracao")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/livros", "/livros/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categorias", "/categorias/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/categorias").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/categorias/{id}").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/categorias/{id}").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/livros").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/livros/{id}").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/livros/{id}").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH, "/livros/{id}/ocultar", "/livros/{id}/disponibilizar")
                        .hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/usuarios").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH, "/usuarios/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/emprestimos").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/penalidades/minhas").authenticated()
                        .requestMatchers(HttpMethod.GET, "/penalidades").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/penalidades/verificar-atrasos").hasRole(Role.ADMIN.name())
                        .requestMatchers("/emprestimos/**").authenticated()
                        .anyRequest().authenticated())
                .userDetailsService(userDetailsService)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(corsAllowedOrigins);

        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"));

        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}