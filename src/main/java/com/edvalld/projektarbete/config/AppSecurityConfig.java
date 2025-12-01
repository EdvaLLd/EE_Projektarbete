package com.edvalld.projektarbete.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

    private final UserDetailsService userDetailsService;    // CustomUserDetailsService
    private final String rememberMeKey;

    @Autowired
    public AppSecurityConfig(
            UserDetailsService userDetailsService,
            @Value("{remember.me.key}") String rememberMeKey    // Constructor Param (property-driven) App.properties
    ) {
        this.userDetailsService = userDetailsService;
        this.rememberMeKey = rememberMeKey;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        /* TODO - If we try to move to a resource that isn't available, we have to login to get a 404
         *   This is unclear and can be made better
         *   Why login to see a 404? Is this secure?
         * */

        // TODO Memory Storage Attack - https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/erasure.html

        httpSecurity
                //.csrf(csrfConfigurer -> csrfConfigurer.disable())   // Disable for DEBUGGING PURPOSES
                .authorizeHttpRequests( auth -> auth
                        // .requestMatchers() // TODO - check against specific HTTP METHOD
                        .requestMatchers("/", "/register", "/static/**").permitAll()  // Allow localhost:8080/
                        .requestMatchers("/debug/**").permitAll()                     // RestController for Debugging
                        .requestMatchers("/admin", "/tools").hasRole("ADMIN")
                        //.requestMatchers("/user").hasRole(UserRole.USER.name())
                        .anyRequest().authenticated() // MUST exist AFTER matchers, TODO - Is this true by DEFAULT?
                )

                // TODO - Logging for Authentication
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                                .loginPage("/login").permitAll()
                                .loginProcessingUrl("/authenticate") // default is /login for processing auth
                                .usernameParameter("username")  // Must match HTML param
                                .passwordParameter("password")
                                .failureUrl("/login?error")
                                .defaultSuccessUrl("/", false)      // Redirect after login->home (false by default)
                        // .failureForwardUrl("")                                   // Handle Login Attempts
                        // .successForwardUrl("")                                   // Handles Success Logic
                )

                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutUrl("/logout").permitAll()
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .logoutSuccessUrl("/login?logout")          // Redirect -> Login
                )

                .rememberMe(rememberMeConfigurer -> rememberMeConfigurer
                        .key(rememberMeKey)
                        .rememberMeParameter("remember-me")           // remember-me default
                        .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(24)) // 24 days
                        .userDetailsService(userDetailsService) // Use Our CustomUser Implementation
                )

                .sessionManagement(session -> session
                        // How long an inactive session lasts
                        // .invalidSessionUrl("/login?invalid")                     // Prevent Login Problems
                        .maximumSessions(1)                         // 🔒 Max 1 concurrent session per user
                        .maxSessionsPreventsLogin(false)     // false = old session invalidated
                        .expiredUrl("/login?expired")                    // redirect if user logs in elsewhere
                );


        return httpSecurity.build();
    }

}
