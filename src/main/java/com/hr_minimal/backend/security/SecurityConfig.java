package com.hr_minimal.backend.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;

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
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	///// FilterChain /////
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
				// basic.disable();
			}
		});

		http.oauth2ResourceServer(new Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>>() {

			@Override
			public void customize(OAuth2ResourceServerConfigurer<HttpSecurity> token) {
				token.jwt(new Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer>() {
					
					@Override
					public void customize(OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer t) {
						// TODO Auto-generated method stub
						
					}
				});

			}
		});

		return http.build();

	}

	///// PasswordEncoder /////
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	///// Authentication /////
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder encoder,
			UserDetailsService userDetailsService) throws Exception {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(encoder);

		AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

		builder.authenticationProvider(provider);

		return builder.build();

	}

	///// JWT /////

	// create RSA Key and store it in a KeyPair
	@Bean
	public KeyPair keyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		return keyPairGenerator.generateKeyPair();
	}

	// create RSAKey-Object because it's used instead of KeyPair
	@Bean
	public RSAKey rsaKey(KeyPair keyPair) {
		return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()).privateKey(keyPair.getPrivate())
				.keyID(UUID.randomUUID().toString()).build();
	}

	// JWK = RSAKey in JSON Format
	// JWKSet = List of RSAKeys
	// JWKSource = RSAKey Source
	@Bean
	public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
		JWKSet jwkSet = new JWKSet(rsaKey);

		JWKSource<SecurityContext> jwkSource = new JWKSource<>() {

			@Override
			public List<JWK> get(JWKSelector jwkSelector, SecurityContext context) throws KeySourceException {
				return jwkSelector.select(jwkSet);
			}
		};

		return jwkSource;
	}

	// decoder for checking tokens
	@Bean
	public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {
		return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
	}

	// encoder to create tokens
	@Bean
	public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
		return new NimbusJwtEncoder(jwkSource);
	}
}
