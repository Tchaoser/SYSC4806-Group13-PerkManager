package com.example.perkmanager.config;

import com.example.perkmanager.security.AccountDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/perks/add").authenticated() //require logged in to create perks
                        .requestMatchers("/perks/*").permitAll() //avoid csrf issues with js
                        .anyRequest().permitAll() //allow access to all other urls
                )
                .formLogin(form -> form
                        .loginPage("/login") //set Spring login page to /login
                        .defaultSuccessUrl("/perks", true) //redirect to /perks upon login
                        .permitAll() //always allow permission to login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") //set Spring logout page to /logout
                        .logoutSuccessUrl("/") //redirect to root upon logout
                        .permitAll() //always allow permission to logout
                );


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(AccountDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
