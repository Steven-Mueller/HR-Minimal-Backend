package com.hr_minimal.backend.controllers;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr_minimal.backend.entities.UserEntity;
import com.hr_minimal.backend.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final UserService service;
	private JwtEncoder jwtEncoder;

	public UserController(UserService service, JwtEncoder jwtEncoder) {
		this.service = service;
		this.jwtEncoder = jwtEncoder;
	}
	
	record JwtResponse(String token) {};

	// gets jwt back just for testing
	@PostMapping("/auth")
	public JwtResponse authenticate(Authentication authentication) {
		return new JwtResponse(createToken(authentication));
	}
	
	private String createToken(Authentication authentication) {
		var claims = JwtClaimsSet.builder()
			.issuer("self")
			.issuedAt(Instant.now())
			.expiresAt(Instant.now().plusSeconds(60 * 30))
			.subject(authentication.getName())
			.claim("scope", createScope(authentication))
			.build();
		
		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	private String createScope(Authentication authentication) {
		return authentication.getAuthorities().stream()
			.map(a -> a.getAuthority())
			.collect(Collectors.joining(" "));
	}
	 

	@GetMapping()
	public List<UserEntity> getAllUser() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public UserEntity getUserById(@PathVariable Long id) {
		return service.findById(id).orElse(null);
	}

	@PostMapping("/register")
	public UserEntity createUser(@RequestBody UserEntity user) {
		return service.create(user);
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id) {
		service.delete(id);
	}
}
