package org.example.backendclerkio.config;

import lombok.AllArgsConstructor;
import org.example.backendclerkio.JwtAuthenticationEntryPoint;
import org.example.backendclerkio.JwtFilter;
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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration implements WebMvcConfigurer {
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    private JwtFilter filter;
    private static PasswordEncoder passwordEncoder;
    @Bean
    public static PasswordEncoder passwordEncoder() {
        if(passwordEncoder==null){
            passwordEncoder = new BCryptPasswordEncoder();
        }
        return passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("WebSec configure(HttpSecurity) Call: 2");

        http.cors().and().csrf().disable() // Disable CORS and CSRF for simplicity; adjust for production
                .authorizeHttpRequests()
                // Permit these endpoints for everyone
                .requestMatchers(
                        "/api/v1/products/**",
                        "/api/v1/products/{id}",
                        "/api/v1/products/create",
                        "/api/v1/products/{id}/delete",
                        "/api/v1/products/{id}/update",
                        "/api/v1/users",
                        "/api/v1/users/{userId}",
                        "/api/v1/users/register",
                        "/api/v1/users/login",
                        "/api/v1/users/{usermail}/setadmin",
                        "/api/v1/users/{usermail}/user",
                        "/api/v1/users/{userId}/update",
                        "/api/v1/users/{userId}/updatepassword",
                        "/api/v1/users/{userId}/delete",
                        "/api/v1/users/logout",
                        "/api/vi/order/cart",
                        "/api/v1/order/delete",
                        "/api/v1/products/{id}/update/stock",
                        )

                .permitAll()
                // Allow DELETE and PUT for authenticated users (no roles required)
                .requestMatchers(HttpMethod.DELETE, "/api/v1/{id}/delete").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/v1/{id}/update").permitAll()
                // Permit all other requests for now (can be refined based on needs)
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Adding the filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("addCorsMappings called");
        registry.addMapping("/**")  // /** means match any string recursively
                .allowedOriginPatterns("http://localhost:*") //Multiple strings allowed. Wildcard * matches all port numbers.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS") // decide which methods to allow
                .allowCredentials(true);
    }

}