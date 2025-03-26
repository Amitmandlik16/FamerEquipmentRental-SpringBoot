package com.farmer.service;

import com.farmer.dto.EquipmentDTO;
import com.farmer.entity.Equipment;
import com.farmer.repository.EquipmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EquipmentService {

	private final EquipmentRepository equipmentRepository;

	public String getRecommendedEquipment(String typeOfWork, String farmSize, String latitude, String longitude) {
		List<Equipment> equipmentList = equipmentRepository.findAll();

		int user_category = equipmentCategoryMap.getOrDefault(typeOfWork, -1);
		int user_farm_type = equipmentFarmSizeMap.getOrDefault(farmSize, -1);

		System.out.println("\n Equipments data fetched from database");

		List<EquipmentDTO> equipments = new ArrayList<>();
		for (Equipment equipment : equipmentList) {
			EquipmentDTO equipmentDTO = new EquipmentDTO();
			equipmentDTO.setId(equipment.getId());
			equipmentDTO.setCategory(equipmentCategoryMap.getOrDefault(equipment.getCategory(), -1));
			equipmentDTO.setFarm_type(equipmentFarmSizeMap.getOrDefault(equipment.getFarmSize(), -1));
			equipmentDTO.setQuality(equipmentQualityMap.getOrDefault(equipment.getEquipmentCondition(), -1));
			equipmentDTO.setLongitude(longitude != null ? Double.parseDouble(longitude.toString()) : null);
			equipmentDTO.setLatitude(latitude != null ? Double.parseDouble(latitude.toString()) : null);
			if (equipmentDTO.getCategory() != -1 && equipmentDTO.getFarm_type() != -1
					&& equipmentDTO.getQuality() != -1) {
				equipments.add(equipmentDTO);
			}
		}

		try {
			Map<String, Object> logData = new HashMap<>();
			logData.put("request_id", "req_" + UUID.randomUUID());
			logData.put("user_category", user_category);
			logData.put("user_farm_type", user_farm_type);
			logData.put("user_latitude", latitude);
			logData.put("user_longitude", longitude);
			logData.put("equipments", equipments);

			String jsonLog = new ObjectMapper().writeValueAsString(logData);
			System.out.println("\n Request Json Created");
			System.out.println(jsonLog);

			// Send API Request
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> requestEntity = new HttpEntity<>(jsonLog, headers);

			ResponseEntity<String> responseEntity = restTemplate.exchange(
					"https://ultrazone-production.up.railway.app/api/recommend/", HttpMethod.POST, requestEntity,
					String.class);

			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				String responseJson = responseEntity.getBody();
				System.out.println("\n Response Received:");
				System.out.println(responseJson);

				return responseJson;
			} else {
				System.err.println("Failed to fetch recommendations. HTTP Status: " + responseEntity.getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null; // Return null if request fails
	}

	// ✅ Mappings for Equipment_Category
	private static final Map<String, Integer> equipmentCategoryMap = new HashMap<>() {
		{
			put("Plowing", 0);
			put("Sowing", 1);
			put("Irrigation", 2);
			put("Fertilizers", 3);
			put("Pesticides", 4);
			put("Harvesting", 5);
			put("Post-Harvesting", 6);
			put("Land-Leveling", 7);
			put("Mulching", 8);
			put("Transport", 9);
			put("Green House", 10);
			put("Orchard", 11);
			put("Fodder Cultivation", 12);
			put("Livestock Farming", 13);
			put("Other", 14);
		}
	};

	// ✅ Mappings for Equipment_Farm_Size
	private static final Map<String, Integer> equipmentFarmSizeMap = new HashMap<>() {
		{
			put("Small", 0);
			put("Medium", 1);
			put("Large", 2);
		}
	};

	// ✅ Mappings for Quality_of_Equipment
	private static final Map<String, Integer> equipmentQualityMap = new HashMap<>() {
		{
			put("Best_Equipment", 2);
			put("Good_Equipment", 1);
			put("Average_Equipment", 0);
		}
	};

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
		return equipmentRepository.findByOwnerId(farmerId);
	}

	// ✅ Get Equipment by ID
	public Equipment getEquipmentById(Long id) {
		return equipmentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Equipment not found with ID: " + id));
	}
}
