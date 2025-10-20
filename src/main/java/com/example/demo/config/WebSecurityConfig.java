package com.example.demo.config;

import com.example.demo.security.CustomSuccessHandler;
import com.example.demo.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomSuccessHandler customSuccessHandler;

    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService,
                             CustomSuccessHandler customSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customSuccessHandler = customSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // ======= PUBLIC PATHS =======
                .requestMatchers(
                    "/", "/trang-chu", "/gioi-thieu", "/tin-tuc", "/lien-he", "/san-pham",
                    "/register/**", "/auth/**",
                    "/css/**", "/js/**", "/images/**", "/assets/**", "/webjars/**", "/favicon.ico"
                ).permitAll()

                // ======= CUSTOMER API =======
                .requestMatchers("/api/customer/delete").hasAuthority("MANAGER")
                .requestMatchers("/api/customer/assignment").hasAuthority("MANAGER")
                .requestMatchers("/api/customer/**").hasAnyAuthority("STAFF", "MANAGER", "ADMIN")

                // ======= ADMIN PAGES =======
                .requestMatchers("/admin/home").hasAnyAuthority("MANAGER", "STAFF", "ADMIN")
                .requestMatchers("/admin/customer-edit", "/admin/customer-edit-*").hasAnyAuthority("STAFF", "MANAGER")
                .requestMatchers("/admin/customer-list", "/admin/profile", "/admin/profile-password").hasAnyAuthority("STAFF", "MANAGER", "ADMIN")
                .requestMatchers("/admin/**").hasAnyAuthority("MANAGER", "STAFF")
                
                // ======= BUILDING =======
                .requestMatchers("/admin/building-list").hasAnyAuthority("STAFF","MANAGER","ADMIN")
                .requestMatchers("/admin/building-edit", "/admin/building-edit-*").hasAnyAuthority("MANAGER","ADMIN")
                .requestMatchers("/api/building/**").hasAnyAuthority("STAFF","MANAGER","ADMIN")
                .requestMatchers("/api/building/assignment", "/api/building/delete", "/api/building/*/delete").hasAnyAuthority("MANAGER","ADMIN")


                // ======= EVERYTHING ELSE =======
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(customSuccessHandler)
                .defaultSuccessUrl("/trang-chu", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
