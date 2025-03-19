package com.farmer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmer.entity.Farmer;

import java.util.List;
import java.util.Optional;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {
	Optional<Farmer> findByUsername(String username);

	Optional<Farmer> findByUsernameAndEmail(String username, String email);

	boolean existsByUsername(String username);

	boolean existsByMobileNumber(String mobileNumber);

	boolean existsByEmail(String email);

}