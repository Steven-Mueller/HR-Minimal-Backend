package com.hr_minimal.backend.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserEntity {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String role;

    private String name;

    private LocalDate birthday;

    // Constructors
    public UserEntity() {
	super();
    }

    public UserEntity(Long id, String email, String password, String role, String name, LocalDate birthday) {
	super();
	this.id = id;
	this.email = email;
	this.password = password;
	this.role = role;
	this.name = name;
	this.birthday = birthday;
    }

    // Getter & Setter
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getRole() {
	return role;
    }

    public void setRole(String role) {
	this.role = role;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public LocalDate getBirthday() {
	return birthday;
    }

    public void setBirthday(LocalDate birthday) {
	this.birthday = birthday;
    }

    @Override
    public String toString() {
	return "UserEntity [id=" + id + ", email=" + email + ", password=" + password + ", role=" + role + ", name="
		+ name + ", birthday=" + birthday + "]";
    }

}
