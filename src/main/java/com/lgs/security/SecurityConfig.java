package com.lgs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                        .requestMatchers("/api/register").permitAll()  // Permite acesso ao endpoint /register sem autenticação
                        .requestMatchers(HttpMethod.GET, "/api/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/services").hasAuthority("CLIENT")
                        .requestMatchers(HttpMethod.PUT, "/api/services/**").hasAuthority("CLIENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/services/**").hasAuthority("CLIENT") // ou outra regra
                        .requestMatchers(HttpMethod.GET,"/api/professionals/me").hasAuthority("PROFESSIONAL") // ou outra regra
                        .requestMatchers(HttpMethod.POST, "/api/professionals/{email}/services/{serviceId}/accept").hasAuthority("PROFESSIONAL") // Aceitar serviço
                        .requestMatchers(HttpMethod.POST, "/api/professionals/{email}/services/{serviceId}/reject").hasAuthority("PROFESSIONAL") // Recusar serviço
                        .requestMatchers(HttpMethod.PUT, "/api/services/{serviceId}/finalizar").hasAuthority("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/client").hasAuthority("CLIENT")
                        .requestMatchers(HttpMethod.PUT, "/api/services/{serviceId}/avaliarCliente").hasAuthority("PROFESSIONAL")
                        .requestMatchers(HttpMethod.POST, "/api/services/{professionalEmail}/solicitar/{serviceId}").hasAuthority("PROFESSIONAL")
                        .requestMatchers(HttpMethod.GET, "/api/services/{serviceId}/profissionais").hasAuthority("CLIENT")
                        .requestMatchers(HttpMethod.POST, "api/services/{ProfessionalEmail}/services/{serviceId}/reject").hasAuthority("CLIENT")
                        .requestMatchers(HttpMethod.POST, "api/services/{ProfessionalEmail}/services/{serviceId}/accept").hasAuthority("CLIENT")
                        .requestMatchers(HttpMethod.GET, "api/users/{email}").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/users2/{email}").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("*"));  // Permite todas as origens
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        source.registerCorsConfiguration("/**", config);  // Aplica a configuração a todas as rotas
        return source;
    } 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}