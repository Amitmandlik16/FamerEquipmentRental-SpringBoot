package com.farmer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmer.dto.FileResponseDTO;
import com.farmer.entity.Equipment;
import com.farmer.service.EquipmentService;
import com.farmer.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/farmer/equipment")
@RequiredArgsConstructor
public class EquipmentController {

	private final EquipmentService equipmentService;
	private final FileService fileService;

	// ✅ Register New Equipment API
	@PostMapping(value = "/register", consumes = "multipart/form-data")
	public ResponseEntity<Equipment> registerEquipment(@RequestPart("equipment") String equipmentJson,
			@RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {

		// Convert JSON string to Equipment object
		ObjectMapper objectMapper = new ObjectMapper();
		Equipment equipment = objectMapper.readValue(equipmentJson, Equipment.class);

		// Upload images and get IDs (max 5 images)
		if (images != null && images.length > 0) {
			List<Long> imageIds = Arrays.stream(images).limit(5).map(file -> {
				try {
					FileResponseDTO fileResponse = fileService.uploadFile(file);
					return fileResponse.getId();
				} catch (IOException e) {
					throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename());
				}
			}).collect(Collectors.toList());

			// Join image IDs as comma-separated values and store in equipment
			equipment.setImageIds(String.join(",", imageIds.stream().map(String::valueOf).toArray(String[]::new)));
		}

		// Save equipment and return response
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

	// Helper method to generate image URLs
	private List<String> getImageUrls(String imageIds) {
		if (imageIds == null || imageIds.isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.stream(imageIds.split(",")).map(id -> "/api/files/download/" + id).collect(Collectors.toList());
	}
}
