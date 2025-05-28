//package http.app1.domain.controllers;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//@WebMvcTest
//@Import(HomePage.class)
//class HomePageTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Configuration
//    static class TestSecurityConfig {
//        @Bean
//        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//            http.csrf(AbstractHttpConfigurer::disable)
//                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//            return http.build();
//        }
//    }
//
//    @Test
//    void testHomeEndpointReturnsHelloMessage() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string("Hello from home page."));
//    }
//}