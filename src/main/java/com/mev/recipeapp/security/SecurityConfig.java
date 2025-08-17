package com.mev.recipeapp.security;

import com.mev.recipeapp.filters.AuthTokenFilter;
import com.mev.recipeapp.security.jwt.AuthEntryPointJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;

    private final AuthTokenFilter authTokenFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, @Value("${frontend.url}") String frontendUrl) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.configurationSource(corsConfigurationSource(frontendUrl)));
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests((request) ->
            request
                    .requestMatchers("/auth/public/**").permitAll()
                    .requestMatchers("/auth/**").authenticated()
                    .requestMatchers("/recipes/public/**").permitAll()
                    .requestMatchers("/recipes/**").authenticated()
                    .requestMatchers("/categories/public/**").permitAll()
                    .requestMatchers("/categories/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/components/public/**").permitAll()
                    .requestMatchers("/components/**").authenticated()
                    .requestMatchers("/users/**").authenticated()
                    .requestMatchers("/pdf/public/**").permitAll()
                    .requestMatchers("/pdf/**").hasAuthority("ROLE_ADMIN")
                    .anyRequest().permitAll()
        );
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${frontend.url}") String frontendUrl) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUrl, "http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
