package com.pro_pizza.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pro_pizza.domain.User;
import com.pro_pizza.service.UserService;
import com.pro_pizza.service.dto.AdminDTO;
import com.pro_pizza.service.dto.UserDTO;

@RestController
@RequestMapping("/user")
public class UserController {

	private UserService userService;
	private ModelMapper modelMapper;

	public UserController(UserService userService, ModelMapper modelMapper) {
		this.userService = userService;
		this.modelMapper = modelMapper;
	}
	
	
//--------------------- Get All User---------------------------------------->>>
	
	@GetMapping("/auth/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<User> userList = userService.fetchAllUsers();
		List<UserDTO> userDTOList = userList.stream().map(this::convertToDTO).collect(Collectors.toList());
		return new ResponseEntity<>(userDTOList, HttpStatus.OK);
	}
	
	
	
//--------------------- Get User ---------------------------------------->>>
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<UserDTO> getUserById(HttpServletRequest request) {
		Long id = (Long) request.getAttribute("id");
		User user = userService.findById(id);
		UserDTO userDTO = convertToDTO(user);
		return new ResponseEntity<>(userDTO, HttpStatus.OK);
	}

//--------------------- Get User By ID ---------------------------------------->>>
	@GetMapping("/{id}/auth")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> getUserByIdAdmin(@PathVariable Long id) {
		User user = userService.findById(id);
		UserDTO userDTO = convertToDTO(user);
		return new ResponseEntity<>(userDTO, HttpStatus.OK);
	}
	
//----------------Add User ---Admin--------------------------------------------------->>>
	
	@PostMapping("/add")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, Boolean>> addUser(@Valid @RequestBody AdminDTO adminDTO) {
		userService.addUserAuth(adminDTO);
		Map<String, Boolean> map = new HashMap<>();
		map.put("User added successfully", true);
		return new ResponseEntity<>(map, HttpStatus.CREATED);
	}
	
	
	
	private UserDTO convertToDTO(User user) {
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		return userDTO;
	}
}
