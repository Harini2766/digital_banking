package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")

                // ✅ SUCCESS
                .successHandler((request, response, authentication) -> {

                    String role = authentication.getAuthorities().iterator().next().getAuthority();

                    if(role.equals("ROLE_ADMIN")){
                        response.sendRedirect("/admin/dashboard");
                    } else {
                        response.sendRedirect("/dashboard");
                    }
                })

                // 🔥 FAILURE HANDLER (IMPORTANT)
                .failureHandler((request, response, exception) -> {

                    String email = request.getParameter("username");
                    User user = userRepository.findByEmail(email);

                    if(user != null && "BLOCKED".equals(user.getStatus())){
                        response.sendRedirect("/login?error=blocked");
                    } else {
                        response.sendRedirect("/login?error=invalid");
                    }
                })

                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}