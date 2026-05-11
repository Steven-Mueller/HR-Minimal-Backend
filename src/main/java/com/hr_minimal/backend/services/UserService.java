package com.hr_minimal.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hr_minimal.backend.entities.UserEntity;
import com.hr_minimal.backend.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
	this.repo = repo;
    }

    public UserEntity create(UserEntity user) {
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
