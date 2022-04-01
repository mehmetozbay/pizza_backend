package com.pro_pizza.security.config;



import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.pro_pizza.security.AuthEntryPointJwt;
import com.pro_pizza.security.AuthTokenFilter;
import com.pro_pizza.security.service.UserDetailsServiceImpl;

import lombok.AllArgsConstructor;


@Configuration
@AllArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	private final AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean 
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	} 
	
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception{
		return super.authenticationManager();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
	}
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		
		.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll().
		
		antMatchers("pizza/api/register","pizza/api/login","pizza/api/user/**","pizza/api/confirm-account/*")
		.permitAll()
		.anyRequest().authenticated();
		
		
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		
	}
	
	private static final String[] AUTH_LIST = {
			"/v3/api-docs/**",
			"/configuration/ui", 
			"/swagger-resources", 
			"/configuration/security", 
			"/swagger-ui/**", 
			"/webjars/**",
			"/swagger/**"
	};
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(AUTH_LIST);
	}
	
	
}
