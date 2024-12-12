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
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtFilter filter;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("WebSec configure(HttpSecurity) Call: 2");

        http.cors().and().csrf().disable()
                .authorizeHttpRequests()
                // Public endpoints
                .requestMatchers(
                        "/api/v1/users/register",
                        "/api/v1/users/login",
                        "/api/v1/products",
                        "/api/v1/products/{id}",
                        "/api/v1/categories/{categoryID}",
                        "/api/v1/category/categories"



                ).permitAll()

                // Endpoints accessible to authenticated users
                .requestMatchers(
                        "/api/v1/order/cart",
                        "/api/v1/order/checkout",
                        "/api/v1/order/validatePayment",
                        "/api/v1/order/myOrders",
                        "/api/v1/users/*/user"

                ).authenticated()

                // Admin-specific endpoints
                .requestMatchers(HttpMethod.POST, "/api/v1/products/create").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/products/*/update").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/products/*/delete").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/{userId}/update").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{userId}/delete").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "api/v1/users/*/setadmin").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "api/v1/users/*/user").hasRole("ADMIN")
                // Add more admin-specific endpoints as needed

                // Any other request requires authentication
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Adding the JWT filter
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
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:*",
                        "https://3semesterdat23c.github.io",
                        "https://randomshop-gddudvarb6gwb7ep.westeurope-01.azurewebsites.net"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .allowCredentials(true);
    }
}
