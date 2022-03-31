package com.pro_pizza.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pro_pizza.controller.vm.LoginVM;
import com.pro_pizza.domain.ConfirmationToken;
import com.pro_pizza.domain.User;
import com.pro_pizza.exception.ResourceNotFoundException;
import com.pro_pizza.repository.ConfirmationTokenRepository;
import com.pro_pizza.repository.UserRepository;
import com.pro_pizza.security.JwtUtils;
import com.pro_pizza.service.UserService;

@RestController
@RequestMapping
public class UserJWTController {
	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping("/register")
	public ResponseEntity<Map<String, Boolean>> registerUser(@Valid @RequestBody User user) {
		userService.register(user);
		Map<String, Boolean> map = new HashMap<>();
		map.put("Register is Ok", true);
		return new ResponseEntity<>(map, HttpStatus.CREATED);
		// RegisterOK ok=new RegisterOK("Successfull");
		// return new ResponseEntity<>(ok,HttpStatus.CREATED);
	}
//	lhttp://localhost:8081/pizza/api/login/oauth2/code/google

	@PostMapping("/login")
	public ResponseEntity<JWTToken> login(@Valid @RequestBody LoginVM loginVM) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginVM.getEmail(), loginVM.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		
		User user = userRepository.findByEmail(loginVM.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("email not found"));
		if (!user.getEnabled()) {
			new ResourceNotFoundException("Please Confirm your Email");
			return null;
		} else {
			String jwt = jwtUtils.generateToken(authentication);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("Authorization", "Bearer " + jwt);
			return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
		}
	}

//	@RequestMapping(value = "/confirm-account", method = { RequestMethod.GET, RequestMethod.POST })
	@GetMapping("/confirm-account/{confirmationToken}")
	public ResponseEntity<Map<String, Boolean>> confirmUserAccount(@PathVariable String confirmationToken) {
		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
		Map<String, Boolean> map = new HashMap<>();

		if (token != null) {
			User user = userRepository.findByEmail(token.getUser().getEmail())
					.orElseThrow(() -> new ResourceNotFoundException("email not found"));
			user.setEnabled(true);
			userRepository.save(user);
			map.put("Verification is Ok", true);
			return new ResponseEntity<>(map, HttpStatus.OK);

		} else {
			map.put("The link is invalid or broken!", true);
			return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
		}

	}

	static class JWTToken {
		private String token;

		public JWTToken(String token) {
			this.token = token;
		}

		@JsonProperty("jwt_token")
		String getToken() {
			return token;
		}

		void setToken(String token) {
			this.token = token;
		}
	}

	/*
	 * static class RegisterOK{
	 * 
	 * private String message;
	 * 
	 * RegisterOK(String message){ this.setMessage(message); }
	 * 
	 * public String getMessage() { return message; }
	 * 
	 * public void setMessage(String message) { this.message = message; }
	 * 
	 * }
	 */

}
