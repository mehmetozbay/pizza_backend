package com.pro_pizza.domain;

import java.util.HashSet;
import java.util.Set;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(name="tbl_user")
@Entity
/**
 * 
 * @author devops
 *
 */
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message="Please provide not blank firstName")
	@NotNull(message="Please provide your firstName")
	@Size(min=1,max=15,message="FirstName '${validatedValue}' must be between {min} and {max} chracters long")
	@Column(length=15,nullable=false)
	private String firstName;
	
	@NotBlank(message="Please provide not blank lastName")
	@NotNull(message="Please provide your lastName")
	@Size(min=1,max=15,message="lastName '${validatedValue}' must be between {min} and {max} chracters long")
	@Column(length=15,nullable=false)
	private String lastName;
	
	@Email(message="Please provide a valid email")
	@NotNull(message="Please provide your email")
	@Size(min=5,max=100,message="Email '${validatedValue}' must be between {min} and {max} chracters long")
	@Column(length=100,unique = true,nullable=false)
	private String email;
	
	@NotNull(message="Please provide your password")
	@NotBlank(message="Please provide your password")
	@Size(min=4,max=60,message="Password '${validatedValue}' must be between {min} and {max} chracters long")
	@Column(nullable=false,length=255)
	private String password;
	
	// (555) 555 5555
	// 555-555-5555
	// 555.555.5555
	@Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Please provide valid phone number")
	@Column(length=14,nullable=false)
	private String phoneNumber;
	
	@NotBlank(message="Please provide not blank Adress")
	@NotNull(message="Please provide your Address")
	@Size(min=10,max=250,message="Adress '${validatedValue}' must be between {min} and {max} chracters long")
	@Column(length=250,nullable=false)
	private String address;
	
	
	@NotBlank(message="Please provide not blank ZipCode")
	@NotNull(message="Please provide your ZipCode")
	@Size(min=4,max=15,message="ZipCode '${validatedValue}' must be between {min} and {max} chracters long")
	@Column(length=15,nullable=false)
	private String plz;
	
	@Column(nullable = false)
	private Boolean builtIn;
	@Column(nullable = true)
	private Boolean enabled;
	/*
	 * Bir user birden fazla role sahip olabilir. Bu nedenle manytomany yapıldı ve
	 * rolleri tutacak hashset tipinde bir alan eklendi.
	 */

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="tbl_user_role",joinColumns=@JoinColumn(name="user_id"),
	inverseJoinColumns = @JoinColumn(name="role_id"))
	private Set<Role> roles=new HashSet<>();
	
	public User(String firstName, String lastName, String password, 
			String phoneNumber, String email, String address,
			String plz) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.address = address;
		this.plz = plz;
	}

	public void setRoles(Set<Role> roles2) {
		this.roles = roles2;
	}

	public User(String firstName, String lastName, String password, 
			String phoneNumber, String email, String address,
			String plz, Set<Role> roles, Boolean builtIn) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.address = address;
		this.plz = plz;
		this.roles = roles;
		this.builtIn = builtIn;
	}
	
	
	public Set<Role> getRole(){
		return roles;
	}
	
}
