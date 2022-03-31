package com.pro_pizza.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pro_pizza.domain.ConfirmationToken;
import com.pro_pizza.domain.Role;
import com.pro_pizza.domain.User;
import com.pro_pizza.domain.enumeration.UserRole;
import com.pro_pizza.exception.BadRequestException;
import com.pro_pizza.exception.ConflictException;
import com.pro_pizza.exception.ResourceNotFoundException;
import com.pro_pizza.repository.ConfirmationTokenRepository;
import com.pro_pizza.repository.RoleRepository;
import com.pro_pizza.repository.UserRepository;
import com.pro_pizza.service.dto.AdminDTO;

@Transactional
@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    
    @Autowired
    private EmailService emailService;
	
	private final static String USER_NO_FOUND_MSG = "user with id %d not found";
	
	
//-----------------------Register---------------------------------->>>
	
	public void register(User user) throws BadRequestException {

		if (userRepository.existsByEmail(user.getEmail())) {
			throw new ConflictException("Error: Email is already in use");
		}

		String encodedPassword = passwordEncoder.encode(user.getPassword());

		user.setPassword(encodedPassword);
		user.setBuiltIn(false);
		user.setEnabled(false);

		Set<Role> roles = new HashSet<>();
		Role role = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
				.orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

		roles.add(role);

		user.setRoles(roles);
		userRepository.save(user);
		
//		 ConfirmationToken
		 ConfirmationToken confirmationToken = new ConfirmationToken(user);
		 confirmationTokenRepository.save(confirmationToken);
		  SimpleMailMessage mailMessage = new SimpleMailMessage();
          mailMessage.setTo(user.getEmail());
          mailMessage.setSubject("Complete Registration!");
//        mailMessage.setFrom("mehmetoz.ozbay@gmail.com");
          mailMessage.setText("To confirm your account, please click here : "
          +"http://localhost:3002/confirmemail="+confirmationToken.getConfirmationToken());
          emailService.sendEmail(mailMessage);
	}

	
//	------------------------FetchAllUsers--------------------------->>>
	
	public List<User> fetchAllUsers() {
		return userRepository.findAll();
	}

//--------------------- Get User---------------------------------------->>>
	public User findById(Long id) throws ResourceNotFoundException {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NO_FOUND_MSG, id)));
		return user;
	}	
	
//	Add User Admin------------------------------------------------------>>>
	public void addUserAuth(AdminDTO adminDTO) throws BadRequestException {
		Boolean emailExists = userRepository.existsByEmail(adminDTO.getEmail());

		if (emailExists) {
			throw new ConflictException("Error:Email is already in use");
		}

		String encodedPassword = passwordEncoder.encode(adminDTO.getPassword());
		adminDTO.setPassword(encodedPassword);
		adminDTO.setBuiltIn(false);

		Set<String> userRoles = adminDTO.getRoles();
		Set<Role> roles = addRoles(userRoles);
		
		User user=new User(adminDTO.getFirstName(),adminDTO.getLastName(),
				 adminDTO.getPassword(),adminDTO.getPhoneNumber(),adminDTO.getEmail(),
				adminDTO.getAddress(),adminDTO.getPlz(),roles,adminDTO.getBuiltIn());
				
		userRepository.save(user);		
	}
	
	public Set<Role> addRoles(Set<String> userRoles) {
		Set<Role> roles = new HashSet<>();

		if (userRoles == null) {
			Role userRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
					.orElseThrow(() -> new RuntimeException("Error: Role not found"));
			roles.add(userRole);
		} else {
			userRoles.forEach(role -> {
				switch (role) {
				case "Administrator":
					Role adminRole = roleRepository.findByName(UserRole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role not found"));
					roles.add(adminRole);

					break;

				case "Manager":
					Role managerRole = roleRepository.findByName(UserRole.ROLE_MANAGER)
							.orElseThrow(() -> new RuntimeException("Error: Role not found"));
					roles.add(managerRole);
					break;

				default:
					Role customerRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
							.orElseThrow(() -> new RuntimeException("Error: Role not found"));
					roles.add(customerRole);
				}
			});
		}
		return roles;
	}
	
	
}
