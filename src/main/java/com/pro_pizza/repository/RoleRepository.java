package com.pro_pizza.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pro_pizza.domain.Role;
import com.pro_pizza.domain.enumeration.UserRole;



@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	Optional<Role> findByName(UserRole name);
}
