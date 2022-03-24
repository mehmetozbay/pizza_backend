package com.pro_pizza.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pro_pizza.domain.User;
import com.pro_pizza.exception.ConflictException;
import com.pro_pizza.exception.ResourceNotFoundException;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email) throws ResourceNotFoundException;	
	Boolean existsByEmail(String email) throws ConflictException;
}
