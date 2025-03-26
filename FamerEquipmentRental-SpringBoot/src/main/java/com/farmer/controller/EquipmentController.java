package com.farmer.controller;

import com.farmer.entity.Equipment;
import com.farmer.service.EquipmentService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/farmer/equipment")
@RequiredArgsConstructor
public class EquipmentController {

	private final EquipmentService equipmentService;

	@PostMapping("/recommend")
	public ResponseEntity<?> getRecommendedEquipment(@RequestBody Map<String, Object> request) {
		String typeOfWork = (String) request.get("typeOfWork");
		String farmSize = (String) request.get("farmSize");
		String latitude = (String) request.get("latitude");
		String longitude = (String) request.get("longitude");

		Equipment recommendedEquipment = equipmentService.getRecommendedEquipment(typeOfWork, farmSize, latitude,
				longitude);

		if (recommendedEquipment == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Collections.singletonMap("error", "No equipment found for the specified criteria."));
		}
		return ResponseEntity.ok(recommendedEquipment);
	}

	// ✅ Register Equipment with Equipment Condition and Image IDs
	@PostMapping("/register")
	public ResponseEntity<Equipment> registerEquipment(@RequestBody Equipment equipment) {
		Equipment savedEquipment = equipmentService.registerEquipment(equipment);
		return ResponseEntity.ok(savedEquipment);
	}

	// ✅ Get All Equipments by Farmer ID
	@GetMapping("/all/{farmerId}")
	public ResponseEntity<List<Map<String, Object>>> getEquipmentsByFarmerId(@PathVariable Long farmerId) {
		List<Equipment> equipments = equipmentService.getEquipmentsByFarmerId(farmerId);

		List<Map<String, Object>> equipmentList = equipments.stream().map(equipment -> {
			Map<String, Object> equipmentMap = new HashMap<>();
			equipmentMap.put("equipment", equipment);
			equipmentMap.put("imageUrls", getImageUrls(equipment.getImageIds()));
			return equipmentMap;
		}).collect(Collectors.toList());

		return ResponseEntity.ok(equipmentList);
	}

	// ✅ Get Equipment by ID with Image URLs
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getEquipmentById(@PathVariable Long id) {
		Equipment equipment = equipmentService.getEquipmentById(id);

		Map<String, Object> response = new HashMap<>();
		response.put("equipment", equipment);
		response.put("imageUrls", getImageUrls(equipment.getImageIds()));

		return ResponseEntity.ok(response);
	}

	// ✅ Helper method to generate image URLs
	private List<String> getImageUrls(String imageIds) {
		if (imageIds == null || imageIds.isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.stream(imageIds.split(",")).map(id -> "/api/files/download/" + id).collect(Collectors.toList());
	}
}
