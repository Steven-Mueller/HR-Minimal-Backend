package com.hr_minimal.backend.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hr_minimal.backend.entities.UserEntity;
import com.hr_minimal.backend.repositories.UserRepository;

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    public DbUserDetailsService(UserRepository repo) {
	super();
	this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

	Optional<UserEntity> optionalUser = repo.findByEmail(username);
	
	if (!optionalUser.isPresent()) {
	    throw new UsernameNotFoundException("User not found");
	}
	
	UserEntity user = optionalUser.get();
	
	return new DbUser(user);
    }

}
