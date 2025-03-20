package com.farmer.repository;

import com.farmer.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

	List<Equipment> findByFarmerId(Long farmerId);
}
