package com.farmer.service;

import com.farmer.entity.Labor;
import com.farmer.repository.LaborRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaborService {

	@Autowired
	private LaborRepository laborRepository;

	// ✅ Register Labor
	public Labor registerLabor(Labor labor) {
		return laborRepository.save(labor);
	}

	// ✅ Get All Labors
	public List<Labor> getAllLabors() {
		return laborRepository.findAll();
	}

	// ✅ Get Labor by ID
	public Labor getLaborById(Long id) {
		return laborRepository.findById(id).orElseThrow(() -> new RuntimeException("Labor not found with ID: " + id));
	}

	// ✅ Get Labors by Skill
	public List<Labor> getLaborsBySkill(String skill) {
		return laborRepository.findBySkillsContaining(skill);
	}

	// ✅ Get Labors by Location
	public List<Labor> getLaborsByLocation(String location) {
		return laborRepository.findByLocationContaining(location);
	}

	// ✅ Labor Login
	public String loginLabor(String email, String password) {
		Labor labor = laborRepository.findByEmail(email);

		if (labor == null) {
			return "❌ Login failed: No labor found with this email.";
		}
		if (!labor.getPassword().equals(password)) {
			return "❌ Login failed: Incorrect password.";
		}
		return "✅ Login successful! Welcome " + labor.getName()+"\n Labor Id= "+labor.getId();
	}
}
