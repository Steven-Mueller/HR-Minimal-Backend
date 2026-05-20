package com.hr_minimal.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hr_minimal.backend.entities.UserEntity;
import com.hr_minimal.backend.repositories.UserRepository;

@Service
public class UserService {
	private final UserRepository repo;
	private final PasswordEncoder encoder;

	public UserService(UserRepository repo, PasswordEncoder encoder) {
		super();
		this.repo = repo;
		this.encoder = encoder;
	}

	public UserEntity create(UserEntity user) {
		// hashing password
		user.setPassword(encoder.encode(user.getPassword()));

		return repo.save(user);
	}

	public Optional<UserEntity> findById(Long id) {
		return repo.findById(id);
	}

	public List<UserEntity> findAll() {
		return repo.findAll();
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}
}
