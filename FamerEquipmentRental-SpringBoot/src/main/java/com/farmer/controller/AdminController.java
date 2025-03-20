package com.farmer.controller;

import com.farmer.dto.Admin;
import com.farmer.entity.Equipment;
import com.farmer.entity.Farmer;
import com.farmer.service.AdminService;
import com.farmer.service.EquipmentService;
import com.farmer.service.FarmerService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	@Autowired
	private AdminService adminService;

	private final FarmerService farmerService;
	private final EquipmentService equipmentService;

	// ✅ Login API for Admin
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Admin admin) {
		return ResponseEntity.ok(adminService.login(admin));
	}

	// ✅ Get All Farmers
	@GetMapping("/farmers")
	public ResponseEntity<List<Farmer>> getAllFarmers() {
		List<Farmer> farmers = farmerService.getAllFarmers();
		return ResponseEntity.ok(farmers);
	}

	// ✅ Get All Equipments with Image URLs
	@GetMapping("/equipments")
	public ResponseEntity<List<Map<String, Object>>> getAllEquipments() {
		List<Equipment> equipments = equipmentService.getAllEquipments();

		List<Map<String, Object>> equipmentList = equipments.stream().map(equipment -> {
			Map<String, Object> equipmentMap = new HashMap<>();
			equipmentMap.put("equipment", equipment);
			equipmentMap.put("imageUrls", getImageUrls(equipment.getImageIds()));
			return equipmentMap;
		}).collect(Collectors.toList());

		return ResponseEntity.ok(equipmentList);
	}

	// ✅ Helper method to generate image URLs from image IDs
	private List<String> getImageUrls(String imageIds) {
		if (imageIds == null || imageIds.isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.stream(imageIds.split(",")).map(id -> "/api/files/download/" + id).collect(Collectors.toList());
	}
}
