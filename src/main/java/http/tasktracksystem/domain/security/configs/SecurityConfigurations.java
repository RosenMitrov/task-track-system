package http.tasktracksystem.domain.security.configs;

import http.tasktracksystem.domain.security.jwt.AuthTokenFilter;
import http.tasktracksystem.domain.security.jwt.JwtAccessDeniedHandler;
import http.tasktracksystem.domain.security.jwt.JwtAuthEntryPoint;
import http.tasktracksystem.domain.security.user.AppUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurations {
    private static final List<String> SECURED_URLS =
            List.of("/api/v1/tasks",
                    "/api/v1/tasks/**",
                    "/api/v1/task-groups",
                    "/api/v1/users/change/role",
                    "/api/v1/users/remove/**",
                    "/api/v1/task-groups/all");

    private static final List<String> NOT_SECURED =
            List.of("/", "/api/v1/auth/login", "/api/v1/users/register");


    private final AppUserDetailsService appUserDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    //    private final PasswordEncoder passwordEncoder;
    private final AuthTokenFilter authTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthEntryPoint) //401
                        .accessDeniedHandler(jwtAccessDeniedHandler) //403
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SECURED_URLS.toArray(String[]::new)).authenticated()
                        .requestMatchers(NOT_SECURED.toArray(String[]::new)).permitAll()
                        .anyRequest().permitAll()
                )
                .userDetailsService(appUserDetailsService)
//                .authenticationProvider(daoAuthenticationProvider())
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(appUserDetailsService);
//        provider.setPasswordEncoder(passwordEncoder);
//        return provider;
//    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**") // allow all paths
//                        .allowedOrigins("http://localhost:4200") // or "*"
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowedHeaders("*");
//            }
//        };
//    }
}
