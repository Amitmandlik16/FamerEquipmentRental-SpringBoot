package com.farmer.service;

import com.farmer.entity.Equipment;
import com.farmer.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

	private final EquipmentRepository equipmentRepository;

	// ✅ Get All Equipments
	public List<Equipment> getAllEquipments() {
		return equipmentRepository.findAll();
	}

	// ✅ Register New Equipment
	public Equipment registerEquipment(Equipment equipment) {
		return equipmentRepository.save(equipment);
	}

	// ✅ Get All Equipments by Farmer ID
	public List<Equipment> getEquipmentsByFarmerId(Long farmerId) {
		return equipmentRepository.findByFarmerId(farmerId);
	}

	// ✅ Get Equipment by ID
	public Equipment getEquipmentById(Long id) {
		return equipmentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Equipment not found with ID: " + id));
	}
}
