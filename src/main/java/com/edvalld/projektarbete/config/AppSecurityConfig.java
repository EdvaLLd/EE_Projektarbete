package com.edvalld.projektarbete.config;

import com.edvalld.projektarbete.security.jwt.JwtAuthenticationFilter;
import com.edvalld.projektarbete.user.authority.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

    private final UserDetailsService userDetailsService;    // CustomUserDetailsService
    private final String rememberMeKey;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public AppSecurityConfig(
            UserDetailsService userDetailsService,
            @Value("{remember.me.key}") String rememberMeKey,    // Constructor Param (property-driven) App.properties
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.userDetailsService = userDetailsService;
        this.rememberMeKey = rememberMeKey;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        /* TODO - If we try to move to a resource that isn't available, we have to login to get a 404
         *   This is unclear and can be made better
         *   Why login to see a 404? Is this secure?
         * */

        // TODO Memory Storage Attack - https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/erasure.html

        httpSecurity
                .csrf(csrfConfigurer -> csrfConfigurer.disable())   // TODO - JWT, best practice?
                .authorizeHttpRequests( auth -> auth
                        // .requestMatchers() // TODO - check against specific HTTP METHOD
                        .requestMatchers("/", "/register", "/static/**", "/login").permitAll()  // Allow localhost:8080/
                        .requestMatchers("/debug/**").permitAll()                     // RestController for Debugging
                        .requestMatchers("/admin", "/tools").hasRole("ADMIN")
                        .requestMatchers("/user").hasRole(UserRole.USER.name())
                        .anyRequest().authenticated() // MUST exist AFTER matchers, TODO - Is this true by DEFAULT?
                )

                // TODO - If you want (optional), insert configure logic here for CORS

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }


}
