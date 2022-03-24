package com.pro_pizza.service;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pro_pizza.domain.Role;
import com.pro_pizza.domain.User;
import com.pro_pizza.domain.enumeration.UserRole;
import com.pro_pizza.exception.BadRequestException;
import com.pro_pizza.exception.ConflictException;
import com.pro_pizza.exception.ResourceNotFoundException;
import com.pro_pizza.repository.RoleRepository;
import com.pro_pizza.repository.UserRepository;

@Transactional
@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	public void register(User user) throws BadRequestException {

		if (userRepository.existsByEmail(user.getEmail())) {
			throw new ConflictException("Error: Email is already in use");
		}

		String encodedPassword = passwordEncoder.encode(user.getPassword());

		user.setPassword(encodedPassword);
		user.setBuiltIn(false);

		Set<Role> roles = new HashSet<>();
		Role role = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
				.orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

		roles.add(role);

		user.setRoles(roles);
		userRepository.save(user);

	}

	
	
}
