package com.hr_minimal.backend.controllers;

import java.util.List;

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

    public UserController(UserService service) {
	this.service = service;
    }

    @GetMapping()
    public List<UserEntity> getAllUser() {
	return service.findAll();
    }

    @GetMapping("/{id}")
    public UserEntity getUserById(@PathVariable Long id) {
	return service.findById(id).orElse(null);
    }

    @PostMapping()
    public UserEntity createUser(@RequestBody UserEntity user) {
	return service.create(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
	service.delete(id);
    }
}
