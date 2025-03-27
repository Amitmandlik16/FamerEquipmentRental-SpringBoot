package com.farmer.controller;

import com.farmer.entity.Labor;
import com.farmer.service.LaborService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/labor")
@CrossOrigin(origins = "*")
public class LaborController {

	@Autowired
	private LaborService laborService;

	// ✅ Register Labor
	@PostMapping("/register")
	public ResponseEntity<Labor> registerLabor(@RequestBody Labor labor) {
		return ResponseEntity.ok(laborService.registerLabor(labor));
	}

	// ✅ Labor Login API
	@PostMapping("/login")
	public ResponseEntity<Labor> loginLabor(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String password = request.get("password");
		return ResponseEntity.ok(laborService.loginLabor(email, password));
	}

	// ✅ Get All Labors
	@GetMapping("/all")
	public ResponseEntity<List<Labor>> getAllLabors() {
		return ResponseEntity.ok(laborService.getAllLabors());
	}

	// ✅ Get Labor by ID
	@GetMapping("/{id}")
	public ResponseEntity<Labor> getLaborById(@PathVariable Long id) {
		return ResponseEntity.ok(laborService.getLaborById(id));
	}

	// ✅ Get Labors by Skill
	@GetMapping("/skill/{skill}")
	public ResponseEntity<List<Labor>> getLaborsBySkill(@PathVariable String skill) {
		return ResponseEntity.ok(laborService.getLaborsBySkill(skill));
	}

	// ✅ Get Labors by Location
	@GetMapping("/location/{location}")
	public ResponseEntity<List<Labor>> getLaborsByLocation(@PathVariable String location) {
		return ResponseEntity.ok(laborService.getLaborsByLocation(location));
	}
}
