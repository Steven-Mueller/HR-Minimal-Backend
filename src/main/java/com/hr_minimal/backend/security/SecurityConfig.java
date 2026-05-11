package com.hr_minimal.backend.security;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	// disable csrf
	http.csrf(new Customizer<CsrfConfigurer<HttpSecurity>>() {

	    @Override
	    public void customize(CsrfConfigurer<HttpSecurity> csrf) {
		csrf.disable();
	    }
	});

	// cors configuration
	http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {

	    @Override
	    public void customize(CorsConfigurer<HttpSecurity> cors) {
		cors.configurationSource(new CorsConfigurationSource() {

		    @Override
		    public @Nullable CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowedOrigins(List.of("http://localhost:5173"));
			config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
			config.setAllowedHeaders(List.of("Content-Type", "Authorization"));

			return config;
		    }
		});
	    }
	});

	// set sessionCreationPolicy
	http.sessionManagement(new Customizer<SessionManagementConfigurer<HttpSecurity>>() {

	    @Override
	    public void customize(SessionManagementConfigurer<HttpSecurity> session) {
		session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	    }
	});

	// handle authentication
	http.authorizeHttpRequests(
		new Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>() {

		    @Override
		    public void customize(
			    AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
			// permitAll just for testing
			auth.requestMatchers(HttpMethod.POST).permitAll();
			auth.anyRequest().authenticated();
		    }
		});

	// use httpBasic
	http.httpBasic(new Customizer<HttpBasicConfigurer<HttpSecurity>>() {

	    @Override
	    public void customize(HttpBasicConfigurer<HttpSecurity> basic) {
		basic.disable();
	    }
	});

	return http.build();

    }

    // PasswordEncoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
    }

    // Authentication
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder encoder,
	    UserDetailsService userDetailsService) throws Exception {

	DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
	provider.setPasswordEncoder(encoder);

	AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

	builder.authenticationProvider(provider);

	return builder.build();

    }

}
