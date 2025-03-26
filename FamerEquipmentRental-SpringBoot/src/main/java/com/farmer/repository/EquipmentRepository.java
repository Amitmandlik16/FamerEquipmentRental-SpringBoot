package com.farmer.repository;

import com.farmer.dto.EquipmentDTO;
import com.farmer.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

	// ✅ Corrected method name (use 'owner' instead of 'farmerId')
	List<Equipment> findByOwnerId(Long ownerId);

	List<Equipment> findAll();

}
